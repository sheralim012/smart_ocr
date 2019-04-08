package pk.edu.pucit.smartocr;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import pk.edu.pucit.smartocr.utilities.Constants;
import pk.edu.pucit.smartocr.utilities.DateTimeHelper;
import pk.edu.pucit.smartocr.utilities.DirectoryHelper;

public class CameraActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, View.OnClickListener {

    private ImageButton mImageButton;
    private TextureView mTextureView;
    private ImageView imageViewFileManagerActivityCamera, imageViewSettingsActivityCamera;

    private String imageName;
    private String imagePath;

    private boolean hasFlash = false;
    private boolean flashOn = false;
    private Menu resolutionMenu;
    private Size[] outputSizes;
    private int userSelectedResolution = 0;

    private String mCameraId;
    private Size mPreviewSize;
    private int mTotalRotation;
    private HandlerThread mBackgroundHandlerThread;
    private Handler mBackgroundHandler;
    private String filePath;

    private String mImageFileName;
    private Size mImageSize;
    private ImageReader mImageReader;

    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 0;
    private static final int REQUEST_WRITE_STORAGE_PERMISSION_RESULT = 1;
    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAIT_LOCK = 1;
    private int mCaptureState = STATE_PREVIEW;

    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mCaptureRequestBuilder;
    private CameraCaptureSession mPreviewCaptureSession;
    private StreamConfigurationMap map;

    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            setupCamera(width, height);
            connectCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            startPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            camera.close();
            mCameraDevice = null;
        }
    };

    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            mBackgroundHandler.post(new ImageSaver(reader.acquireLatestImage()));
            //sending image to next screen
            Intent intent = new Intent(CameraActivity.this, ImageConfirmationActivity.class);
            intent.putExtra(Constants.IMAGE_NAME_KEY, imageName);
            intent.putExtra(Constants.IMAGE_PATH_KEY, imagePath);
            startActivity(intent);
        }
    };

    private CameraCaptureSession.CaptureCallback mPreviewCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        private void process(CaptureResult captureResult) {
            switch (mCaptureState) {
                case STATE_PREVIEW:
                    //do nothing
                    break;
                case STATE_WAIT_LOCK:
                    try {
                        mCaptureState = STATE_PREVIEW;
                        Integer afState = captureResult.get(CaptureResult.CONTROL_AF_STATE);
                        if (afState <= CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED
                                || afState <= CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED) {
                            startCaptureRequest();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            process(result);
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
        }
    };

    private class ImageSaver implements Runnable {
        private final Image mImage;

        public ImageSaver(Image image) {
            mImage = image;
        }

        @Override
        public void run() {
            ByteBuffer byteBuffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
            FileOutputStream fileOutputStream = null;

            try {
                fileOutputStream = new FileOutputStream(mImageFileName);
                fileOutputStream.write(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initialize();
    }

    public void initialize() {
        //FindViewById
        mImageButton = findViewById(R.id.image_view_camera_activity_camera);
        imageViewSettingsActivityCamera = findViewById(R.id.image_view_settings_activity_camera);
        imageViewFileManagerActivityCamera = findViewById(R.id.image_view_file_manager_activity_camera);
        mTextureView = findViewById(R.id.texture_view_camera_activity_camera);
        resolutionMenu = findViewById(R.id.menu_option_resolution_camera_activity);

        //OnClickListeners
        mImageButton.setOnClickListener(this);
        imageViewSettingsActivityCamera.setOnClickListener(this);
        imageViewFileManagerActivityCamera.setOnClickListener(this);

        hasFlash = this.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_view_file_manager_activity_camera:
                Intent intent = new Intent(this, FileManagerActivity.class);
                startActivity(intent);
                break;
            case R.id.image_view_settings_activity_camera:
                PopupMenu settings_menu_camera_activity = new PopupMenu(CameraActivity.this, v);
                settings_menu_camera_activity.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) this);
                settings_menu_camera_activity.inflate(R.menu.settings_menu_camera_activity);
                settings_menu_camera_activity.show();
                break;
            case R.id.image_view_camera_activity_camera:
                lockFocus();
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_option_resolution_camera_activity:
                //Toast.makeText(getApplicationContext(), "resolution pressed", Toast.LENGTH_LONG).show();
                resolutionMenu = item.getSubMenu();
                //adding resolution sizes in menu
                for (int i = 0; i < outputSizes.length; i++) {
                    MenuItem menuItem = resolutionMenu.add(0, i, 0, outputSizes[i].toString());
                    menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            userSelectedResolution = item.getItemId();
                            //Toast.makeText(getApplicationContext(), String.valueOf(userSelectedResolution) , Toast.LENGTH_LONG).show();
                            mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[userSelectedResolution];
                            startPreview();
                            return false;
                        }
                    });
                }
                return true;
            case R.id.menu_option_flash_camera_activity:
                //Toast.makeText(getApplicationContext(), "flash pressed", Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_option_flash_on_camera_activity:
                //Toast.makeText(getApplicationContext(), "Flash is turned On", Toast.LENGTH_LONG).show();
                flashOn = true;
                return true;
            case R.id.menu_option_flash_off_camera_activity:
                flashOn = false;
                return true;
            case R.id.menu_option_setting_camera_activity:
                Intent intent = new Intent(CameraActivity.this, DefaultSettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //flash off
        flashOn = false;
        //set default resolution
        userSelectedResolution = 0;
        startBackgroundThread();
        if (mTextureView.isAvailable()) {
            setupCamera(mTextureView.getWidth(), mTextureView.getHeight());
            connectCamera();
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }

    }

    @Override
    protected void onPause() {
        closeCamera();

        stopBackgroundThread();
        super.onPause();
    }

    private void setupCamera(int width, int height) {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
                map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                mTotalRotation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                outputSizes = map.getOutputSizes(SurfaceTexture.class);
                mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];
                mImageSize = map.getOutputSizes(ImageFormat.JPEG)[0];
                mImageReader = ImageReader.newInstance(mImageSize.getWidth(), mImageSize.getHeight(), ImageFormat.JPEG, 1);
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);
                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();

        }
    }

    private void connectCamera() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        Toast.makeText(this, "Application requires permission to access Camera", Toast.LENGTH_LONG).show();
                    }
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_RESULT);
                }

            } else {
                cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startPreview() {
        mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[userSelectedResolution];
        SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface previewSurface = new Surface(surfaceTexture);
        try {
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(previewSurface);

            mCameraDevice.createCaptureSession(
                    Arrays.asList(previewSurface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            mPreviewCaptureSession = session;
                            try {
                                mPreviewCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                            Toast.makeText(getApplicationContext(), "Unable to setup Camera Preview", Toast.LENGTH_LONG).show();
                        }
                    }
                    , null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startCaptureRequest() {
        try {
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            mCaptureRequestBuilder.addTarget(mImageReader.getSurface());
            mCaptureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, mTotalRotation);
            CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber);

                    try {
                        createImageFileName();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            mPreviewCaptureSession.capture(mCaptureRequestBuilder.build(), captureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION_RESULT) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Application requires permission to access Camera", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_WRITE_STORAGE_PERMISSION_RESULT) {
            if (!(grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(getApplicationContext(), "Application requires permission to access Storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFileName() throws IOException {
        String currentDateTime = DirectoryHelper.generateFileName("jpg");
        File imageFile = new File(Constants.PICTURES_DIRECTORY, currentDateTime);
        mImageFileName = imageFile.getAbsolutePath();
        filePath = mImageFileName;
        imageName = currentDateTime;
        imagePath = imageFile.getAbsolutePath();
        return imageFile;
    }

    private void lockFocus() {
        mCaptureState = STATE_WAIT_LOCK;
        if (flashOn) {
            mCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
        }
        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);
        try {
            mPreviewCaptureSession.capture(mCaptureRequestBuilder.build(), mPreviewCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    private void startBackgroundThread() {
        mBackgroundHandlerThread = new HandlerThread("cameraActivity");
        mBackgroundHandlerThread.start();
        mBackgroundHandler = new Handler(mBackgroundHandlerThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundHandlerThread.quitSafely();
        try {
            mBackgroundHandlerThread.join();
            mBackgroundHandlerThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}

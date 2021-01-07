package ru.tpu.russiantpu.auth.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.IOException;

import ru.tpu.russiantpu.R;

public class StartFragment extends Fragment implements View.OnClickListener {
    // implement TextureView.SurfaceTextureListener to use TextureView

    private final String fragmentTag = String.valueOf(R.string.prev_auth_frag_tag);

    private Button loginButton;
    private Button gotoRegisterButton;

    private ScalableVideoView videoView;
    //private MediaPlayer mediaPlayer = new MediaPlayer();
    //private AssetFileDescriptor fileDescriptor;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layoutInflater = inflater.inflate(R.layout.fragment_start, container, false);

        loginButton = layoutInflater.findViewById(R.id.goto_login);
        gotoRegisterButton = layoutInflater.findViewById(R.id.goto_register);

        // Включаем видео на заднем плане из ресурсов
        videoView = layoutInflater.findViewById(R.id.video_view);

        //Uri uri = Uri.parse("android.resource://" + requireActivity().getPackageName() + "/" + R.raw.tpu480);

        try {
            videoView.setRawData(R.raw.tpu480);
            videoView.prepare(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoView.setLooping(true);
                    videoView.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        loginButton.setOnClickListener(this);
        gotoRegisterButton.setOnClickListener(this);

        return layoutInflater;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goto_login:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LoginFragment()).addToBackStack(fragmentTag).commit();
                break;
            case R.id.goto_register:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RegisterFragment()).addToBackStack(fragmentTag).commit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Заново запускаем видео
        if (videoView != null) {
            //videoView.start();
        }
    }

/*    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int width, int height) {
        Surface surface = new Surface(surfaceTexture);
        try {
            mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            mediaPlayer.setSurface(surface);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mediaPlayer.setDataSource(fileDescriptor);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.start();
                        mediaPlayer.setLooping(true);
                    }
                });
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }*/
}

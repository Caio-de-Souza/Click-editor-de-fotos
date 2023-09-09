package com.souza.caio.click.telas;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.souza.caio.click.R;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.IOException;

public class LogoActivity extends AppCompatActivity {
    private ScalableVideoView player;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        iniciarComponentes();

        configurarAnimacaoLogo();
        configurarPlayer();
    }

    private void configurarAnimacaoLogo() {
        Animation animacaoSurgir = configurarAnimacao(LogoActivity.this, android.R.anim.fade_in, 2000);
        animacaoSurgir.setStartOffset(10000);
        logo.startAnimation(animacaoSurgir);
    }

    private void configurarPlayer() {
        try {
            player.setRawData(R.raw.click_intro);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (isFinishing()) {
                        return;
                    }
                    encerrarActivity();
                }
            });

            iniciarVideo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void iniciarVideo() throws IOException {
        player.prepare();
        player.setLooping(false);
        player.start();
    }

    private void encerrarActivity() {
        Intent intent = new Intent(LogoActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void iniciarComponentes() {
        player = findViewById(R.id.video_logo_splash_screen);
        logo = findViewById(R.id.logo_dev);
    }

    private Animation configurarAnimacao(Context contexto, int fonte, int duracao) {
        Animation animation = AnimationUtils.loadAnimation(contexto, fonte);
        Interpolator interpolartor = new FastOutSlowInInterpolator();
        animation.setInterpolator(interpolartor);
        animation.setDuration(duracao);

        return animation;
    }
}

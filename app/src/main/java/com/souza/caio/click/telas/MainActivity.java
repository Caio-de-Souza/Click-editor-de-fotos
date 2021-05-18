package com.souza.caio.click.telas;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.souza.caio.click.sensores.BrushFragmentListener;
import com.souza.caio.click.sensores.EditarImagemFragmentListener;
import com.souza.caio.click.sensores.EmojiFragmentListener;
import com.souza.caio.click.sensores.ListaFiltrosFragmentListener;
import com.souza.caio.click.R;
import com.souza.caio.click.adaptadores.BitmapUtils;
import com.souza.caio.click.fragmentos.AdicionarTexto;
import com.souza.caio.click.fragmentos.BrushFragment;
import com.souza.caio.click.fragmentos.EditarImagemFragment;
import com.souza.caio.click.fragmentos.EmojiFragment;
import com.souza.caio.click.fragmentos.ListaFiltrosFragment;
import com.souza.caio.click.sensores.AdicionarTextoFragmentListener;
import com.yalantis.ucrop.UCrop;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.File;
import java.util.List;
import java.util.UUID;

import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class MainActivity extends AppCompatActivity implements ListaFiltrosFragmentListener, EditarImagemFragmentListener, BrushFragmentListener, AdicionarTextoFragmentListener, EmojiFragmentListener
{
    public static final String TITULO_APPBAR = "Click - Editor de Fotos";
    public static final String ERRO_SALVAMENTO = "ERRO: INCAPAZ DE SALVAR A IMAGEM! :X";
    public static final String SALVO_COM_SUCESSO = "Imagem salva na galeria";
    public static final String PERMISSÃO_NEGADA = "Permissão negada!";
    private static final String nomeIcone = "default.jpg";
    private static final int PERMISSAO_SELECIONAR_IMAGEM = 1;
    private static final int PERMISSAO_INSERIR_IMAGEM = 2;
    private static final int CAMERA_REQUEST = 3;

    private PhotoEditorView preview;
    private PhotoEditor editor;

    private CardView efeitos, editar, rabiscar, add_imagem, add_text, add_emoji, cortar;

    private CoordinatorLayout coordinatorLayout;

    private Bitmap imagemOriginal, imagemEditando, imagemFinal;

    private ListaFiltrosFragment filterListFragment;
    private EditarImagemFragment editarImagemFragment;

    private int brilhoFinal = 0;
    private float saturacaoFinal = 1.0f;
    private float contrasteFinal = 1.0f;
    private Uri uri_selecionada;

    public static final String BIBLIOTECA_NATIVA = "NativeImageProcessor";

    static
    {
        System.loadLibrary(BIBLIOTECA_NATIVA);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nomearAppBar();
        iniciarComponentes();
        configurarEditor();
        configurarCliqueBotoes();
        carregarImagem();
    }

    private void configurarCliqueBotoes()
    {
        configurarCliqueBotaoEfeitos();
        configurarCliqueBotaoEditar();
        configurarCliqueBotaoRabiscar();
        configurarCliqueBotaoAddFoto();
        configurarCliqueBotaoAddTexto();
        configurarCliqueBotaoAddEmoji();
        configurarCliqueBotaoCortar();
    }

    public void iniciarComponentes()
    {

        preview = findViewById(R.id.image_preview);
        efeitos = findViewById(R.id.btn_filtros);
        editar = findViewById(R.id.btnEditaImagem);
        rabiscar = findViewById(R.id.btn_pintar);
        add_imagem = findViewById(R.id.btn_add_imagem);
        add_text = findViewById(R.id.btn_add_text);
        add_emoji = findViewById(R.id.btn_add_emoji);
        cortar = findViewById(R.id.btn_cortar);

        coordinatorLayout = findViewById(R.id.layout_editar_capa);
    }

    private void configurarCliqueBotaoCortar()
    {
        cortar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (uri_selecionada != null)
                {
                    iniciarCorte(uri_selecionada);
                }
                else
                {
                    exibirMensagem("Apenas imagens da galeria!");
                }
            }
        });
    }

    private void configurarCliqueBotaoAddEmoji()
    {
        add_emoji.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                exibirPainelAddEmoji();
            }
        });
    }

    private void exibirPainelAddEmoji()
    {
        EmojiFragment emojiFragment = new EmojiFragment().getInstance();
        emojiFragment.setListener(MainActivity.this);
        emojiFragment.show(getSupportFragmentManager(), emojiFragment.getTag());
    }

    private void configurarCliqueBotaoAddTexto()
    {
        add_text.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                exibirPainelAddTexto();
            }
        });
    }

    private void exibirPainelAddTexto()
    {
        AdicionarTexto adicionarTexto = AdicionarTexto.getInstance();
        adicionarTexto.setListener(MainActivity.this);
        adicionarTexto.show(getSupportFragmentManager(), adicionarTexto.getTag());
    }

    private void configurarCliqueBotaoAddFoto()
    {
        add_imagem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addFoto();
            }
        });
    }

    private void configurarCliqueBotaoRabiscar()
    {
        rabiscar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                exibirPainelPincel();
            }
        });
    }

    private void exibirPainelPincel()
    {
        BrushFragment brushFragment = BrushFragment.getInstance();
        brushFragment.setListener(MainActivity.this);
        brushFragment.show(getSupportFragmentManager(), brushFragment.getTag());

        editor.setBrushDrawingMode(true);
    }

    private void configurarCliqueBotaoEditar()
    {
        editar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                configurarEdicaoSimples();
            }
        });
    }

    private void configurarEdicaoSimples()
    {
        EditarImagemFragment editarImagemFragment = EditarImagemFragment.getInstance();
        editarImagemFragment.setListener(MainActivity.this);

        editarImagemFragment.show(getSupportFragmentManager(), editarImagemFragment.getTag());
    }

    private void configurarCliqueBotaoEfeitos()
    {
        efeitos.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                exibirListaFiltros();
            }
        });
    }

    private void exibirListaFiltros()
    {
        if (filterListFragment != null)
        {
            filterListFragment.show(getSupportFragmentManager(), filterListFragment.getTag());
        }
        else
        {
            ListaFiltrosFragment listaFiltrosFragment = ListaFiltrosFragment.getInstance(null);
            listaFiltrosFragment.setListener(MainActivity.this);
            listaFiltrosFragment.show(getSupportFragmentManager(), listaFiltrosFragment.getTag());
        }
    }

    private void configurarEditor()
    {
        editor = new PhotoEditor.Builder(MainActivity.this, preview)
                .setPinchTextScalable(true)
                .setDefaultEmojiTypeface(Typeface.createFromAsset(getAssets(), "emojione-android.ttf"))
                .build();
    }

    private void nomearAppBar()
    {
        getSupportActionBar().setTitle(TITULO_APPBAR);
    }

    private void iniciarCorte(Uri uri)
    {
        String nomeArquivo = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), nomeArquivo)));

        uCrop.start(MainActivity.this);
    }

    private void addFoto()
    {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
              .withListener(new MultiplePermissionsListener()
              {
                  @Override
                  public void onPermissionsChecked(MultiplePermissionsReport report)
                  {
                      if (report.areAllPermissionsGranted())
                      {
                          selecionarImagemGaleria(PERMISSAO_INSERIR_IMAGEM);
                      }
                      else
                      {
                          exibirMensagem("Permissão negada");
                      }
                  }

                  @Override
                  public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token)
                  {
                      token.continuePermissionRequest();
                  }
              }).check();
    }

    private void selecionarImagemGaleria(int permissaoInserirImagem)
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, permissaoInserirImagem);
    }

    private void carregarImagem()
    {
        imagemOriginal = BitmapUtils.getBitmapFromAssets(this, nomeIcone, 300, 300);
        imagemEditando = imagemOriginal.copy(Bitmap.Config.ARGB_8888, true);
        imagemFinal = imagemOriginal.copy(Bitmap.Config.ARGB_8888, true);
        preview.getSource().setImageBitmap(imagemOriginal);
    }


    public String getNomeIcone()
    {
        return nomeIcone;
    }

    @Override
    public void aoMudarBrilho(int brilho)
    {
        brilhoFinal = brilho;
        Filter filtro = new Filter();
        filtro.addSubFilter(new BrightnessSubFilter(brilho));
        preview.getSource().setImageBitmap(filtro.processFilter(imagemFinal.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void aoMudarSaturacao(float saturacao)
    {
        saturacaoFinal = saturacao;
        Filter filtro = new Filter();
        filtro.addSubFilter(new SaturationSubfilter(saturacao));
        preview.getSource().setImageBitmap(filtro.processFilter(imagemFinal.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void aoMudarContraste(float contraste)
    {
        contrasteFinal = contraste;
        Filter filtro = new Filter();
        filtro.addSubFilter(new ContrastSubFilter(contraste));
        preview.getSource().setImageBitmap(filtro.processFilter(imagemFinal.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void aoIniciarEdicao()
    {

    }

    @Override
    public void aoFinalizarEdicao()
    {
        Bitmap bitmap = imagemEditando.copy(Bitmap.Config.ARGB_8888, true);

        Filter filter = new Filter();
        filter.addSubFilter(new BrightnessSubFilter(brilhoFinal));
        filter.addSubFilter(new SaturationSubfilter(saturacaoFinal));
        filter.addSubFilter(new ContrastSubFilter(contrasteFinal));

        imagemFinal = filter.processFilter(bitmap);
    }

    @Override
    public void aoSelecionarFiltro(Filter filtro)
    {
        ///resetarEdicao();
        imagemEditando = imagemOriginal.copy(Bitmap.Config.ARGB_8888, true);
        preview.getSource().setImageBitmap(filtro.processFilter(imagemEditando));
        imagemFinal = imagemEditando.copy(Bitmap.Config.ARGB_8888, true);
    }




   /* public void resetarEdicao()
    {
        if (editarImagemFragment1 = null)

        {
            editarImagemFragment1.resetarEdicao();
        }
        brilhoFinal = 0;
        saturacaoFinal = 1.0f;
        contrasteFinal = 1.0f;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == R.id.abrir_camera)
        {
            abrirCamera();
            return true;
        }
        else if (item.getItemId() == R.id.selecionar_imagem)
        {
            abrirGaleria();
            return true;
        }
        else if (item.getItemId() == R.id.salvar_imagem)
        {
            salvarImagem();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void abrirCamera()
    {
        Dexter.withActivity(this).withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
              .withListener(new MultiplePermissionsListener()
              {
                  @Override
                  public void onPermissionsChecked(MultiplePermissionsReport report)
                  {
                      if (report.areAllPermissionsGranted())
                      {
                          ContentValues values = new ContentValues();
                          values.put(MediaStore.Images.Media.TITLE, "Nova Imagem");
                          values.put(MediaStore.Images.Media.DESCRIPTION, "Da câmera");

                          uri_selecionada = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                          Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                          cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri_selecionada);
                          startActivityForResult(cameraIntent, CAMERA_REQUEST);
                      }
                      else
                      {
                          exibirMensagem("Permissão negada!");
                      }
                  }

                  @Override
                  public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token)
                  {
                      token.continuePermissionRequest();
                  }
              }).check();
    }

    public void abrirGaleria()
    {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
              .withListener(new MultiplePermissionsListener()
              {
                  @Override
                  public void onPermissionsChecked(MultiplePermissionsReport report)
                  {
                      if (report.areAllPermissionsGranted())
                      {
                          selecionarImagemGaleria(PERMISSAO_SELECIONAR_IMAGEM);
                      }
                      else
                      {
                          exibirMensagem("Permissão negada!");
                      }
                  }

                  @Override
                  public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token)
                  {
                  }
              }).check();
    }

    public void salvarImagem()
    {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
              .withListener(new MultiplePermissionsListener()
              {
                  @Override
                  public void onPermissionsChecked(MultiplePermissionsReport report)
                  {
                      if (report.areAllPermissionsGranted())
                      {

                          editor.saveAsBitmap(new OnSaveBitmap()

                          {
                              @Override
                              public void onBitmapReady(Bitmap saveBitmap)
                              {
                                  preview.getSource().setImageBitmap(saveBitmap);
                                  final String diretorio = BitmapUtils.inserirImagem(getContentResolver(), saveBitmap, System.currentTimeMillis() + "_cover.jpg", null);
                                  if (!TextUtils.isEmpty(diretorio))
                                  {
                                      Snackbar snackbar = Snackbar.make(coordinatorLayout, SALVO_COM_SUCESSO, Snackbar.LENGTH_LONG)
                                                                  .setAction("Abrir", new View.OnClickListener()
                                                                  {
                                                                      @Override
                                                                      public void onClick(View v)
                                                                      {
                                                                          abrirImagem(diretorio);
                                                                      }
                                                                  });
                                      snackbar.show();
                                  }
                                  else
                                  {
                                      Snackbar snackbar = Snackbar.make(coordinatorLayout, ERRO_SALVAMENTO, Snackbar.LENGTH_LONG);
                                      snackbar.show();
                                  }
                              }

                              @Override
                              public void onFailure(Exception e)
                              {

                              }
                          });
                      }
                      else
                      {
                          exibirMensagem(PERMISSÃO_NEGADA);
                      }
                  }

                  @Override
                  public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token)
                  {
                      token.continuePermissionRequest();
                  }
              }).check();
    }

    public void abrirImagem(String imagem)
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(imagem), "image/*");
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (resultCode == RESULT_OK && requestCode == PERMISSAO_SELECIONAR_IMAGEM && data != null)
        {
            // preview.getSource().setImageURI(data.getData());

            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 1000, 1400);

            uri_selecionada = data.getData();
            processarImagemRecebida(bitmap);
            recarregarPainelFiltros();
        }
        else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK)
        {
            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, uri_selecionada, 1000, 1400);
            processarImagemRecebida(bitmap);
            recarregarPainelFiltros();
        }
        else if (resultCode == RESULT_OK && requestCode == PERMISSAO_INSERIR_IMAGEM && data != null)
        {
            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 400, 400);
            editor.addImage(bitmap);
        }
        //   super.onActivityResult(requestCode, resultCode, data);
        else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK)
        {
            handleCropResult(data);
        }
        else if (requestCode == UCrop.RESULT_ERROR)
        {
            handleCropError(data);
        }
    }

    private void recarregarPainelFiltros()
    {
        filterListFragment = ListaFiltrosFragment.getInstance(imagemOriginal);
        filterListFragment.setListener(this);
    }

    private void processarImagemRecebida(Bitmap bitmap)
    {
        imagemOriginal.recycle();
        imagemEditando.recycle();
        imagemFinal.recycle();
        imagemOriginal = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        imagemEditando = imagemOriginal.copy(Bitmap.Config.ARGB_8888, true);
        imagemFinal = imagemOriginal.copy(Bitmap.Config.ARGB_8888, true);

        preview.getSource().setImageBitmap(imagemOriginal);

        bitmap.recycle();
    }

    public void exibirMensagem(String mensagem)
    {
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }

    private void handleCropError(Intent data)
    {
        final Throwable error = UCrop.getError(data);
        if (error != null)
        {
            exibirMensagem(error.getMessage());
        }
        else
        {
            exibirMensagem("Erro desconhecido! :X");
        }
    }


    private void handleCropResult(Intent data)
    {
        final Uri resultUri = UCrop.getOutput(data);

        if (resultUri != null)
        {
            preview.getSource().setImageURI(resultUri);
        }
        else
        {
            exibirMensagem("Não foi possível carregar a imagem cortada! :X");
        }
    }

    @Override
    public void aoMudarEspessuraPincel(float tamanho)
    {
        editor.setBrushSize(tamanho);
    }

    @Override
    public void aoMudarOpacidadePincel(int opacidade)
    {
        editor.setOpacity(opacidade);
    }

    @Override
    public void aoMudarCor(int cor)
    {
        editor.setBrushColor(cor);
    }

    @Override
    public void aoMudarEstadoPincel(boolean estaApagando)
    {
        if (estaApagando)
        {
            editor.brushEraser();
        }
        else
        {
            editor.setBrushDrawingMode(true);
        }
    }


    @Override
    public void aoSelecionarEmoji(String emoji)
    {
        editor.addEmoji(emoji);
    }

    @Override
    public void aoClicarBtnAddTexto(Typeface typeface, String texto, int cor)
    {
        editor.addText(typeface, texto, cor);
    }
}

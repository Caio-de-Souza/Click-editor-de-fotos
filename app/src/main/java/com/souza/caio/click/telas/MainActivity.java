package com.souza.caio.click.telas;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.souza.caio.click.exceptions.ai.AIException;
import com.souza.caio.click.sensores.BrushFragmentListener;
import com.souza.caio.click.sensores.EditarImagemFragmentListener;
import com.souza.caio.click.sensores.EmojiFragmentListener;
import com.souza.caio.click.sensores.ListaFiltrosFragmentListener;
import com.souza.caio.click.R;
import com.souza.caio.click.adaptadores.BitmapUtils;
import com.souza.caio.click.fragmentos.AdicionarTextoFragment;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.souza.caio.click.ai.connection.ClickAiClient.getInstance;
import static com.souza.caio.click.ai.utils.ClickAIUtils.validateImageReimaginate;
import static com.souza.caio.click.ai.utils.ClickAIUtils.validateImageRemoveBackground;
import static com.souza.caio.click.ai.utils.ClickAIUtils.validateImageRemoveText;
import static com.souza.caio.click.ai.utils.ClickAIUtils.validateImageReplaceBackground;
import static com.souza.caio.click.utils.Utils.readApiKey;
import static com.souza.caio.click.utils.Utils.showMessage;

public class MainActivity extends AppCompatActivity implements ListaFiltrosFragmentListener, EditarImagemFragmentListener, BrushFragmentListener, AdicionarTextoFragmentListener, EmojiFragmentListener {
    public static final String TITULO_APPBAR = "Click - Editor de Fotos";
    public static final String ERRO_SALVAMENTO = "ERRO: INCAPAZ DE SALVAR A IMAGEM! :X";
    public static final String SALVO_COM_SUCESSO = "Imagem salva na galeria";
    public static final String PERMISSAO_NEGADA = "Permissão negada!";
    private static final String nomeIcone = "default.jpg";
    private static final int PERMISSAO_SELECIONAR_IMAGEM = 1;
    private static final int PERMISSAO_INSERIR_IMAGEM = 2;
    private static final int CAMERA_REQUEST = 3;

    private PhotoEditorView preview;
    private PhotoEditor editor;

    private CardView efeitos, editar, rabiscar,
            addImage, addText, addEmoji, cortar,
            redimensionar, reimaginar, removerFundo,
            removerTexto, substituirFundo, textoParaImagem;

    private CoordinatorLayout coordinatorLayout;

    private Bitmap imagemOriginal, imagemEditando, imagemFinal;

    private ListaFiltrosFragment filterListFragment;
    private EditarImagemFragment editarImagemFragment;

    private int brilhoFinal = 0;
    private float saturacaoFinal = 1.0f;
    private float contrasteFinal = 1.0f;
    private Uri uri_selecionada;

    private EmojiFragment emojiFragment;
    private AdicionarTextoFragment adicionarTextoFragment;
    private BrushFragment brushFragment;
    private ProgressDialog progressDialog;

    private String API_KEY;

    public static final String BIBLIOTECA_NATIVA = "NativeImageProcessor";

    static {
        System.loadLibrary(BIBLIOTECA_NATIVA);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        API_KEY = readApiKey(getApplicationContext());

        nomearAppBar();
        iniciarComponentes();
        configurarEditor();
        configurarCliqueBotoes();
        carregarImagem();
    }

    private void configurarCliqueBotoes() {
        configurarCliqueBotaoEfeitos();
        configurarCliqueBotaoEditar();
        configurarCliqueBotaoRabiscar();
        configurarCliqueBotaoAddFoto();
        configurarCliqueBotaoAddTexto();
        configurarCliqueBotaoAddEmoji();
        configurarCliqueBotaoCortar();
        configurarCliqueBotaoRedimensionar();
        configurarCliqueBotaoReimaginar();
        configurarCliqueBotaoRemoverFundo();
        configurarCliqueBotaoRemoverTexto();
        configurarCliqueBotaoSubstituirFundo();
        configurarCliqueTextoParaImagem();
    }

    private void configurarCliqueTextoParaImagem() {
        textoParaImagem.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.layout_prompt_ai, null);
            final EditText editText = dialogView.findViewById(R.id.editTextPromptAI);

            builder.setView(dialogView)
                    .setTitle("Insira a descrição da imagem a ser gerada")
                    .setPositiveButton("Gerar", (dialog, which) -> {
                        String promptDigitado = editText.getText().toString();
                        if(promptDigitado.isEmpty()){
                            showMessage(MainActivity.this, "O texto é obrigatório");
                            return;
                        }

                        try{
                            progressDialog.setMessage("Gerando imagem a partir de texto com IA...");
                            progressDialog.show();
                            textToImage(promptDigitado);
                        }catch(AIException e){
                            e.printStackTrace();
                            showMessage(MainActivity.this, e.getMessage());
                            progressDialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void configurarCliqueBotaoSubstituirFundo() {
        substituirFundo.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.layout_prompt_ai, null);
            final EditText editText = dialogView.findViewById(R.id.editTextPromptAI);

            builder.setView(dialogView)
                    .setTitle("Insira a descrição do novo fundo")
                    .setPositiveButton("Processar", (dialog, which) -> {
                        String promptDigitado = editText.getText().toString();

                        try{
                            progressDialog.setMessage("substituindo fundo de imagem com IA...");
                            progressDialog.show();
                            replaceBackground(promptDigitado);
                        }catch(AIException e){
                            e.printStackTrace();
                            showMessage(MainActivity.this, e.getMessage());
                            progressDialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void configurarCliqueBotaoRemoverTexto() {
        removerTexto.setOnClickListener(v -> {
            try{
                progressDialog.setMessage("removendo texto de imagem com IA...");
                progressDialog.show();
                removeText();
            }catch(AIException e){
                e.printStackTrace();
                showMessage(MainActivity.this, e.getMessage());
                progressDialog.dismiss();
            }
        });
    }

    private void configurarCliqueBotaoRemoverFundo() {
        removerFundo.setOnClickListener(v -> {
            try{
                progressDialog.setMessage("removendo fundo de imagem com IA...");
                progressDialog.show();
                removeBackground();
            }catch(AIException e){
                e.printStackTrace();
                showMessage(MainActivity.this, e.getMessage());
                progressDialog.dismiss();
            }
        });
    }

    private void configurarCliqueBotaoReimaginar() {
        reimaginar.setOnClickListener(v -> {
            try{
                progressDialog.setMessage("Reimaginando imagem com IA...");
                progressDialog.show();
                reimaginarImagem();
            }catch(AIException e){
                e.printStackTrace();
                showMessage(MainActivity.this, e.getMessage());
                progressDialog.dismiss();
            }
        });
    }

    private void configurarCliqueBotaoRedimensionar() {
        redimensionar.setOnClickListener(v -> resizeImage());
    }

    public void iniciarComponentes() {

        preview = findViewById(R.id.image_preview);
        efeitos = findViewById(R.id.btn_filtros);
        editar = findViewById(R.id.btnEditaImagem);
        rabiscar = findViewById(R.id.btn_pintar);
        addImage = findViewById(R.id.btn_add_imagem);
        addText = findViewById(R.id.btn_add_text);
        addEmoji = findViewById(R.id.btn_add_emoji);
        cortar = findViewById(R.id.btn_cortar);
        redimensionar = findViewById(R.id.btn_redimensionar);
        reimaginar = findViewById(R.id.btn_reimaginar);
        removerFundo = findViewById(R.id.btn_remove_background);
        removerTexto = findViewById(R.id.btn_remove_text);
        substituirFundo = findViewById(R.id.btn_replace_background);
        textoParaImagem = findViewById(R.id.btn_text_to_image);

        coordinatorLayout = findViewById(R.id.layout_editar_capa);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private void configurarCliqueBotaoCortar() {
        cortar.setOnClickListener(v -> {
            if (uri_selecionada != null) {
                iniciarCorte(uri_selecionada);
            } else {
                showMessage(MainActivity.this, "Apenas imagens da galeria!");
            }
        });
    }

    private void configurarCliqueBotaoAddEmoji() {
        addEmoji.setOnClickListener(v -> exibirPainelAddEmoji());
    }

    private void exibirPainelAddEmoji() {
        emojiFragment = EmojiFragment.getInstance();
        emojiFragment.setListener(MainActivity.this);
        emojiFragment.show(getSupportFragmentManager(), emojiFragment.getTag());
    }

    private void configurarCliqueBotaoAddTexto() {
        addText.setOnClickListener(v -> exibirPainelAddTexto());
    }

    private void exibirPainelAddTexto() {
        adicionarTextoFragment = AdicionarTextoFragment.getInstance();
        adicionarTextoFragment.setListener(MainActivity.this);
        adicionarTextoFragment.show(getSupportFragmentManager(), adicionarTextoFragment.getTag());
    }

    private void configurarCliqueBotaoAddFoto() {
        addImage.setOnClickListener(v -> addFoto());
    }

    private void configurarCliqueBotaoRabiscar() {
        rabiscar.setOnClickListener(v -> exibirPainelPincel());
    }

    private void exibirPainelPincel() {
        brushFragment = BrushFragment.getInstance();
        brushFragment.setListener(MainActivity.this);
        brushFragment.show(getSupportFragmentManager(), brushFragment.getTag());

        editor.setBrushDrawingMode(true);
    }

    private void configurarCliqueBotaoEditar() {
        editar.setOnClickListener(v -> configurarEdicaoSimples());
    }

    private void configurarEdicaoSimples() {
        editarImagemFragment = EditarImagemFragment.getInstance();
        editarImagemFragment.setListener(MainActivity.this);
        editarImagemFragment.show(getSupportFragmentManager(), editarImagemFragment.getTag());
    }

    private void configurarCliqueBotaoEfeitos() {
        efeitos.setOnClickListener(v -> exibirListaFiltros());
    }

    private void exibirListaFiltros() {
        if (filterListFragment != null) {
            filterListFragment.show(getSupportFragmentManager(), filterListFragment.getTag());
        } else {
            ListaFiltrosFragment listaFiltrosFragment = ListaFiltrosFragment.getInstance(null);
            listaFiltrosFragment.setListener(MainActivity.this);
            listaFiltrosFragment.show(getSupportFragmentManager(), listaFiltrosFragment.getTag());
        }
    }

    private void configurarEditor() {
        editor = new PhotoEditor.Builder(MainActivity.this, preview)
                .setPinchTextScalable(true)
                .setDefaultEmojiTypeface(Typeface.createFromAsset(getAssets(), "emojione-android.ttf"))
                .build();
    }

    private void nomearAppBar() {
        getSupportActionBar().setTitle(TITULO_APPBAR);
    }

    private void iniciarCorte(Uri uri) {
        String nomeArquivo = UUID.randomUUID().toString() + ".jpg";
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), nomeArquivo)));
        uCrop.start(MainActivity.this);
    }

    private void addFoto() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            selecionarImagemGaleria(PERMISSAO_INSERIR_IMAGEM);
                        } else {
                            showMessage(MainActivity.this, "Permissão negada");
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void selecionarImagemGaleria(int permissaoInserirImagem) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, permissaoInserirImagem);
    }

    private void carregarImagem() {
        imagemOriginal = BitmapUtils.getBitmapFromAssets(this, nomeIcone, 300, 300);
        assert imagemOriginal != null;
        imagemEditando = imagemOriginal.copy(Bitmap.Config.ARGB_8888, true);
        imagemFinal = imagemOriginal.copy(Bitmap.Config.ARGB_8888, true);
        preview.getSource().setImageBitmap(imagemOriginal);
    }


    public String getNomeIcone() {
        return nomeIcone;
    }

    @Override
    public void aoMudarBrilho(int brilho) {
        brilhoFinal = brilho;
        Filter filtro = new Filter();
        filtro.addSubFilter(new BrightnessSubFilter(brilho));
        preview.getSource().setImageBitmap(filtro.processFilter(imagemFinal.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void aoMudarSaturacao(float saturacao) {
        saturacaoFinal = saturacao;
        Filter filtro = new Filter();
        filtro.addSubFilter(new SaturationSubfilter(saturacao));
        preview.getSource().setImageBitmap(filtro.processFilter(imagemFinal.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void aoMudarContraste(float contraste) {
        contrasteFinal = contraste;
        Filter filtro = new Filter();
        filtro.addSubFilter(new ContrastSubFilter(contraste));
        preview.getSource().setImageBitmap(filtro.processFilter(imagemFinal.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void aoIniciarEdicao() {

    }

    @Override
    public void aoFinalizarEdicao() {
        Bitmap bitmap = imagemEditando.copy(Bitmap.Config.ARGB_8888, true);

        Filter filter = new Filter();
        filter.addSubFilter(new BrightnessSubFilter(brilhoFinal));
        filter.addSubFilter(new SaturationSubfilter(saturacaoFinal));
        filter.addSubFilter(new ContrastSubFilter(contrasteFinal));

        imagemFinal = filter.processFilter(bitmap);
    }

    @Override
    public void aoSelecionarFiltro(Filter filtro) {
        ///resetarEdicao();
        imagemEditando = imagemOriginal.copy(Bitmap.Config.ARGB_8888, true);
        preview.getSource().setImageBitmap(filtro.processFilter(imagemEditando));
        imagemFinal = imagemEditando.copy(Bitmap.Config.ARGB_8888, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.btnOpenCamera) {
            abrirCamera();
        } else if (item.getItemId() == R.id.btnSelectImage) {
            abrirGaleria();
        } else if (item.getItemId() == R.id.btnSaveImage) {
            salvarImagem();
        } else if (item.getItemId() == R.id.btnResetar) {
            resetarEdicao();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void abrirCamera() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE, "Nova Imagem");
                            values.put(MediaStore.Images.Media.DESCRIPTION, "Da câmera");

                            uri_selecionada = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri_selecionada);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        } else {
                            showMessage(MainActivity.this, "Permissão negada!");
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public void abrirGaleria() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            selecionarImagemGaleria(PERMISSAO_SELECIONAR_IMAGEM);
                        } else {
                            showMessage(MainActivity.this, "Permissão negada!");
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    }
                }).check();
    }

    public void salvarImagem() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            editor.saveAsBitmap(new OnSaveBitmap() {
                                @Override
                                public void onBitmapReady(Bitmap saveBitmap) {
                                    preview.getSource().setImageBitmap(saveBitmap);
                                    final String diretorio = BitmapUtils.inserirImagem(MainActivity.this, getContentResolver(), saveBitmap, System.currentTimeMillis() + "_cover.jpg", null);
                                    if (!TextUtils.isEmpty(diretorio)) {
                                        Snackbar snackbar = Snackbar.make(coordinatorLayout, SALVO_COM_SUCESSO, Snackbar.LENGTH_LONG)
                                                .setAction("Abrir", v -> abrirImagem(diretorio));
                                        snackbar.show();
                                    } else {
                                        Snackbar snackbar = Snackbar.make(coordinatorLayout, ERRO_SALVAMENTO, Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {

                                }
                            });
                        } else {
                            showMessage(MainActivity.this, PERMISSAO_NEGADA);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public void abrirImagem(String imagem) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(imagem), "image/*");
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == PERMISSAO_SELECIONAR_IMAGEM && data != null) {
            // preview.getSource().setImageURI(data.getData());

            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 1000, 1400);

            uri_selecionada = data.getData();
            assert bitmap != null;
            processarImagemRecebida(bitmap);
            recarregarPainelFiltros();
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, uri_selecionada, 1000, 1400);
            assert bitmap != null;
            processarImagemRecebida(bitmap);
            recarregarPainelFiltros();
        } else if (resultCode == RESULT_OK && requestCode == PERMISSAO_INSERIR_IMAGEM && data != null) {
            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 400, 400);
            editor.addImage(bitmap);
        }
        //   super.onActivityResult(requestCode, resultCode, data);
        else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            handleCropResult(data);
        } else if (requestCode == UCrop.RESULT_ERROR) {
            handleCropError(data);
        }
    }

    private void recarregarPainelFiltros() {
        filterListFragment = ListaFiltrosFragment.getInstance(imagemOriginal);
        filterListFragment.setListener(this);
    }

    private void processarImagemRecebida(Bitmap bitmap) {
        imagemOriginal.recycle();
        imagemEditando.recycle();
        imagemFinal.recycle();
        imagemOriginal = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        imagemEditando = imagemOriginal.copy(Bitmap.Config.ARGB_8888, true);
        imagemFinal = imagemOriginal.copy(Bitmap.Config.ARGB_8888, true);

        preview.getSource().setImageBitmap(imagemOriginal);

        bitmap.recycle();
    }

    private void handleCropError(Intent data) {
        final Throwable error = UCrop.getError(data);
        if (error != null) {
            showMessage(MainActivity.this, error.getMessage());
        } else {
            showMessage(MainActivity.this, "Erro desconhecido! :X");
        }
    }


    private void handleCropResult(Intent data) {
        final Uri resultUri = UCrop.getOutput(data);

        if (resultUri != null) {
            preview.getSource().setImageURI(resultUri);
        } else {
            showMessage(MainActivity.this, "Não foi possível carregar a imagem cortada! :X");
        }
    }

    @Override
    public void aoMudarEspessuraPincel(float tamanho) {
        editor.setBrushSize(tamanho);
    }

    @Override
    public void aoMudarOpacidadePincel(int opacidade) {
        editor.setOpacity(opacidade);
    }

    @Override
    public void aoMudarCor(int cor) {
        editor.setBrushColor(cor);
    }

    @Override
    public void aoMudarEstadoPincel(boolean estaApagando) {
        if (estaApagando) {
            editor.brushEraser();
        } else {
            editor.setBrushDrawingMode(true);
        }
    }


    @Override
    public void aoSelecionarEmoji(String emoji) {
        editor.addEmoji(emoji);
        emojiFragment.dismissAllowingStateLoss();
    }

    @Override
    public void aoClicarBtnAddTexto(Typeface typeface, String texto, int cor) {
        editor.addText(typeface, texto, cor);
        adicionarTextoFragment.resetarAlteracoes();
        adicionarTextoFragment.dismissAllowingStateLoss();
    }

    public void resetarEdicao() {
        editor.clearAllViews();
        brilhoFinal = 100;
        contrasteFinal = 0.0f;
        saturacaoFinal = 10.0f;
        imagemFinal = imagemOriginal;

        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackEntryCount = fragmentManager.getBackStackEntryCount();

        for (int i = 0; i < backStackEntryCount; i++) {
            fragmentManager.popBackStackImmediate();
        }

        if (editarImagemFragment != null) {
            editarImagemFragment.resetarAlteracoes();
        }

        if (brushFragment != null) {
            brushFragment.resetarAlteracoes();
        }

        if (adicionarTextoFragment != null) {
            adicionarTextoFragment.resetarAlteracoes();
        }
    }

    private void resizeImage() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imagemEditando.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bitmapData = byteArrayOutputStream.toByteArray();

        int widthToUpscale = 300; //TODO Caio change it to dinamically
        int heightToUpscale = 300; //TODO Caio change it to dinamically

        RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/jpeg"), bitmapData);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image_file", "image.jpg", imageRequestBody);
        MultipartBody.Part targetWidth = MultipartBody.Part.createFormData("target_width", String.valueOf(widthToUpscale));
        MultipartBody.Part targetHeight = MultipartBody.Part.createFormData("target_width", String.valueOf(heightToUpscale));

        Call<ResponseBody> call = getInstance().getApi().upscaleImage(API_KEY, imagePart, targetWidth, targetHeight);

        Log.i("AI Conn", "connecting...");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        byte[] imageResized = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageResized, 0, imageResized.length);

                        processarImagemRecebida(bitmap);
                        recarregarPainelFiltros();

                        Log.i("success", "Success");
                    } else {
                        throw new IOException();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    showMessage(MainActivity.this, "Erro ao redimensionar Imagem com IA.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showMessage(MainActivity.this, t.getMessage());
            }
        });
    }

    private void reimaginarImagem() {
        validateImageReimaginate(imagemEditando);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imagemEditando.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bitmapData = byteArrayOutputStream.toByteArray();

        RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/jpeg"), bitmapData);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image_file", "image.jpg", imageRequestBody);

        Call<ResponseBody> call = getInstance().getApi().reimagineImage(API_KEY, imagePart);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        byte[] imageResized = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageResized, 0, imageResized.length);
                        processarImagemRecebida(bitmap);
                        recarregarPainelFiltros();
                    } else {
                        throw new IOException();
                    }
                    progressDialog.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                    showMessage(MainActivity.this, "Erro ao reimaginar imagem com IA.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                showMessage(MainActivity.this, t.getMessage());
            }
        });
    }

    private void removeBackground() {
        validateImageRemoveBackground(imagemEditando);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imagemEditando.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bitmapData = byteArrayOutputStream.toByteArray();

        RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/jpeg"), bitmapData);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image_file", "image.jpg", imageRequestBody);

        Call<ResponseBody> call = getInstance().getApi().removeBackgroundImage(API_KEY, imagePart);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                for(String header: response.headers().names()){
                    Log.i(header, response.headers().get(header));
                }

                try {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        byte[] imageResized = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageResized, 0, imageResized.length);
                        processarImagemRecebida(bitmap);
                        recarregarPainelFiltros();
                    } else {
                        throw new IOException();
                    }
                    progressDialog.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                    showMessage(MainActivity.this, "Erro ao remover fundo de imagem com IA.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                showMessage(MainActivity.this, t.getMessage());
            }
        });
    }

    private void removeText() {
        validateImageRemoveText(imagemEditando);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imagemEditando.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bitmapData = byteArrayOutputStream.toByteArray();

        RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/jpeg"), bitmapData);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image_file", "image.jpg", imageRequestBody);

        Call<ResponseBody> call = getInstance().getApi().removeTextImage(API_KEY, imagePart);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                for(String header: response.headers().names()){
                    Log.i(header, response.headers().get(header));
                }

                try {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        byte[] imageResized = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageResized, 0, imageResized.length);
                        processarImagemRecebida(bitmap);
                        recarregarPainelFiltros();
                    } else {
                        throw new IOException();
                    }
                    progressDialog.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                    showMessage(MainActivity.this, "Erro ao remover texto de imagem com IA.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                showMessage(MainActivity.this, t.getMessage());
            }
        });
    }

    private void replaceBackground(String prompt) {
        validateImageReplaceBackground(imagemEditando);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imagemEditando.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bitmapData = byteArrayOutputStream.toByteArray();

        RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/jpeg"), bitmapData);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image_file", "image.jpg", imageRequestBody);

        prompt = prompt == null ? "" : prompt;
        Log.i("targetPrompt", prompt);
        MultipartBody.Part targetPrompt = MultipartBody.Part.createFormData("prompt", prompt);

        Call<ResponseBody> call = getInstance().getApi().replaceBackground(API_KEY, imagePart, targetPrompt);

        Log.i("ReplaceBackground", "Substituindo fundo...");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("statusResponse", response.code() + "");
                for(String header: response.headers().names()){
                    Log.i(header, response.headers().get(header));
                }

                try {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        byte[] imageResized = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageResized, 0, imageResized.length);
                        processarImagemRecebida(bitmap);
                        recarregarPainelFiltros();
                    } else {
                        Log.i("Erro", response.errorBody().string());
                        throw new IOException();
                    }
                    progressDialog.dismiss();
                } catch (IOException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                    showMessage(MainActivity.this, "Erro ao substituir fundo de imagem com IA.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("Erro", t.getMessage());
                showMessage(MainActivity.this, t.getMessage());
            }
        });
    }

    private void textToImage(String prompt) {
        Log.i("targetPrompt", prompt);
        MultipartBody.Part targetPrompt = MultipartBody.Part.createFormData("prompt", prompt);

        Call<ResponseBody> call = getInstance().getApi().textToImage(API_KEY, targetPrompt);

        Log.i("ReplaceBackground", "Text to Image...");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("statusResponse", response.code() + "");
                for(String header: response.headers().names()){
                    Log.i(header, response.headers().get(header));
                }

                try {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        byte[] imageResized = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageResized, 0, imageResized.length);
                        processarImagemRecebida(bitmap);
                        recarregarPainelFiltros();
                    } else {
                        Log.i("Erro", response.errorBody().string());
                        throw new IOException();
                    }
                    progressDialog.dismiss();
                } catch (IOException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                    showMessage(MainActivity.this, "Erro ao gerar imagem com base em texto com IA.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("Erro", t.getMessage());
                showMessage(MainActivity.this, t.getMessage());
            }
        });
    }
}

package com.souza.caio.click.fragmentos;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.souza.caio.click.adaptadores.AdapterThumbnail;
import com.souza.caio.click.adaptadores.BitmapUtils;
import com.souza.caio.click.adaptadores.DecorateSpace;
import com.souza.caio.click.sensores.ListaFiltrosFragmentListener;
import com.souza.caio.click.telas.MainActivity;
import com.souza.caio.click.R;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;

public class ListaFiltrosFragment extends BottomSheetDialogFragment implements ListaFiltrosFragmentListener
{

    private View view;
    private RecyclerView recyclerView;
    private AdapterThumbnail adaptadorThumb;
    private List<ThumbnailItem> thumbnailItems;

    private ListaFiltrosFragmentListener listener;

    static ListaFiltrosFragment instance;
    static Bitmap bitmap;


    public ListaFiltrosFragment()
    {
        // Required empty public constructor
    }

    public static ListaFiltrosFragment getInstance(Bitmap bitmapSaved)
    {
        bitmap = bitmapSaved;
        if (instance == null)
        {
            instance = new ListaFiltrosFragment();
        }
        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_lista_filtros, container, false);
        iniciarComponentes();
        return view;
    }

    public void iniciarComponentes()
    {
        thumbnailItems = new ArrayList<>();
        adaptadorThumb = new AdapterThumbnail(thumbnailItems, this, getActivity());

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        int espaco = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new DecorateSpace(espaco));

        recyclerView.setAdapter(adaptadorThumb);

        displayThumbNail(bitmap);
    }

    public void displayThumbNail(final Bitmap bitmap)
    {
        Runnable r = new Runnable()
        {
            @Override
            public void run()
            {
                Bitmap thumbImg;
                if (bitmap == null)
                    thumbImg = BitmapUtils.getBitmapFromAssets(getActivity(), ((MainActivity) getActivity()).getNomeIcone(), 100, 100);

                else
                    thumbImg = Bitmap.createScaledBitmap(bitmap, 100, 100, false);

                if (thumbImg == null)
                    return;
                ThumbnailsManager.clearThumbs();
                thumbnailItems.clear();


                ThumbnailItem thumbnailItem = new ThumbnailItem();
                thumbnailItem.image = thumbImg;
                thumbnailItem.filterName = "Normal";
                ThumbnailsManager.addThumb(thumbnailItem);


                List<Filter> filters = FilterPack.getFilterPack(getActivity());

                for (Filter filter : filters)
                {
                    ThumbnailItem thumb = new ThumbnailItem();
                    thumb.image = thumbImg;
                    thumb.filter = filter;
                    thumb.filterName = filter.getName();
                    ThumbnailsManager.addThumb(thumb);
                }
                thumbnailItems.addAll(ThumbnailsManager.processThumbs(getActivity()));

                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        adaptadorThumb.notifyDataSetChanged();
                    }
                });
            }
        };

        new Thread(r).start();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public ListaFiltrosFragmentListener getListener()
    {
        return listener;
    }

    public void setListener(ListaFiltrosFragmentListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void aoSelecionarFiltro(Filter filtro)
    {
        if (listener != null)
        {
            listener.aoSelecionarFiltro(filtro);
        }
    }
}

package com.anshusingh.fileexplorer.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anshusingh.fileexplorer.Class.FileOpener;
import com.anshusingh.fileexplorer.FileAdapter;
import com.anshusingh.fileexplorer.OnFileSelectedListner;
import com.anshusingh.fileexplorer.R;
import com.google.android.material.dialog.MaterialDialogs;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InternalFragment extends Fragment implements OnFileSelectedListner {

    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private List<File> fileList;
    private ImageView img_back;
    protected TextView tv_pathHolder;
    File storage;

    String data;
    String[] items ={"Details", "Rename","Delete", "Share"};
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragement_internal,container, false);

        recyclerView = view.findViewById(R.id.recycler_internal);
         img_back = view.findViewById(R.id.img_back);
         tv_pathHolder = view.findViewById(R.id.tv_pathHolder);

         String internalStorage = System.getenv("EXTERNAL_STORAGE");
         storage = new File(internalStorage);

         try{
             data = getArguments().getString("path");
             File file = new File(data);
             storage = file;
         }catch (Exception ex){
             Log.e("RecyclerOnClicked",ex.toString());
             ex.printStackTrace();

         }

         tv_pathHolder.setText(storage.getAbsolutePath());
        runtimePermission();
         return view;
    }

    private void runtimePermission(){
        Dexter.withContext(getContext()).withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                displayFiles();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    public ArrayList<File> findFiles(File file){
        ArrayList<File>  arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        for(File singleFile : files){

            if(singleFile.isDirectory() && !singleFile.isHidden()){
                arrayList.add(singleFile);
            }
        }

        for(File singleFile :  files){
            if(singleFile.getName().toLowerCase().endsWith(".jpeg") || singleFile.getName().toLowerCase().endsWith(".jpg") ||
                    singleFile.getName().toLowerCase().endsWith(".png") || singleFile.getName().toLowerCase().endsWith(".mp3") ||
                    singleFile.getName().toLowerCase().endsWith(".wav") || singleFile.getName().toLowerCase().endsWith(".mp4") ||
                    singleFile.getName().toLowerCase().endsWith(".pdf") || singleFile.getName().toLowerCase().endsWith(".doc")||
                    singleFile.getName().toLowerCase().endsWith(".apk"))
            {
                    arrayList.add(singleFile);
            }
        }

        return arrayList;
    }

    private  void displayFiles(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        fileList = new ArrayList<>();
        fileList.addAll(findFiles(storage));
        fileAdapter = new FileAdapter(getContext(),fileList,this);
        recyclerView.setAdapter(fileAdapter);
    }

    @Override
    public void onFileClicked(File file) {
        if(file.isDirectory()){
            Bundle bundle = new Bundle();
            bundle.putString("path", file.getAbsolutePath());
            InternalFragment internalFragment = new InternalFragment();
            internalFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, internalFragment).addToBackStack(null).commit();

        }else{
            try {
                FileOpener.openFile(getContext(), file);
            } catch (IOException e) {
                Log.e("FileOpen",e.toString());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFileLongClicked(File file) {

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.option_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setTitle("Selecct Options");
        ListView options = dialog.findViewById(R.id.list);
        OptionsAdapter optionsAdapter = new OptionsAdapter();
        options.setAdapter(optionsAdapter);
        dialog.show();
    }
    class OptionsAdapter  extends BaseAdapter{

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return items[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.option_layout, null);
            TextView textView = view.findViewById(R.id.tv_option);
            ImageView imageView = view.findViewById(R.id.imgOption);

            textView.setText(items[i]);
            if(items[i].equals("Details")){
                imageView.setImageResource(R.drawable.ic_baseline_error_outline_24);

            } else if(items[i].equals("Rename")){
                imageView.setImageResource(R.drawable.ic_rename);
            }
            else if(items[i].equals("Delete")){
                imageView.setImageResource(R.drawable.ic_delete);
            }
            else if(items[i].equals("Share")){
                imageView.setImageResource(R.drawable.ic_share);
            }
            return view;
        }
    }
}

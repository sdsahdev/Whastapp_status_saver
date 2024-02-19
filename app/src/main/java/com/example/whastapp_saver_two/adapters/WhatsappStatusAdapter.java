package com.example.whastapp_saver_two.adapters;

import static com.example.whastapp_saver_two.util_items.Utils.RootDirectoryWhatsappShow;
import static com.example.whastapp_saver_two.util_items.Utils.createFileFolder;
import static com.example.whastapp_saver_two.util_items.Utils.shareImage;
import static com.example.whastapp_saver_two.util_items.Utils.shareVideo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whastapp_saver_two.R;
import com.example.whastapp_saver_two.VideoPlayerActivity;
import com.example.whastapp_saver_two.all_interfaces.FileListClickInterface;
import com.example.whastapp_saver_two.all_interfaces.FileListWhatsappClickInterface;
import com.example.whastapp_saver_two.databinding.ItemsWhatsappViewBinding;
import com.example.whastapp_saver_two.util_items.Utils;
import com.example.whastapp_saver_two.wa_model.WhatsappStatusModel;
import com.bumptech.glide.Glide;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class WhatsappStatusAdapter extends RecyclerView.Adapter<WhatsappStatusAdapter.ViewHolder> {
    private Context context;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    private ArrayList<File> fileArrayList3;
    private ArrayList<WhatsappStatusModel> fileArrayList;
    private LayoutInflater layoutInflater;
    ProgressDialog dialogProgress;
    String fileName = "";
    public String saveFilePath = RootDirectoryWhatsappShow + File.separator;
    private FileListWhatsappClickInterface fileListClickInterface;
    private FileListClickInterface fileListClickInterface2;

    public WhatsappStatusAdapter(Context context, ArrayList<WhatsappStatusModel> files) {
        this.context = context;
        this.fileArrayList = files;
        initProgress();
    }

    public WhatsappStatusAdapter(Context context, ArrayList<WhatsappStatusModel> files, FileListWhatsappClickInterface fileListClickInterface) {
        this.context = context;
        this.fileArrayList = files;
        this.fileListClickInterface = fileListClickInterface;
        initProgress();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        return new ViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.items_whatsapp_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        WhatsappStatusModel fileItem = fileArrayList.get(i);
//        File fileItem112 = fileArrayList3.get(i);
        if (fileItem.getUri().toString().endsWith(".mp4")) {
            viewHolder.binding.ivPlay.setVisibility(View.VISIBLE);
        } else {
            viewHolder.binding.ivPlay.setVisibility(View.GONE);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            Glide.with(context)
                    .load(fileItem.getUri())
                    .into(viewHolder.binding.pcw);
        } else {
            Glide.with(context)
                    .load(fileItem.getPath())
                    .into(viewHolder.binding.pcw);
        }

        viewHolder.binding.pcw.setOnClickListener(v -> {
            String extension = fileArrayList.get(i).getFilename().substring(fileArrayList.get(i).getFilename().lastIndexOf("."));
            if (extension.equals(".mp4")) {

                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("PathVideo", fileItem.getUri().toString());
                context.startActivity(intent);
            } else {
                Log.d("inpass", "onBindViewHolder: pass");
                final String path = String.valueOf(fileItem.getUri());
                Log.d("checkfiles", "onCreate: "+path);
                Intent intent=new Intent(context, com.example.whastapp_saver_two.ImageView.class);
                intent.putExtra("file",path);
                context.startActivity(intent);
            }


//            Toast.makeText(context, "click", Toast.LENGTH_SHORT).show();
//            final String path = fileArrayList.get(i).getPath();
//                Intent intent=new Intent(context, com.example.whastapp_saver_two.ImageView.class);
//                intent.putExtra("file",path);
//                context.startActivity(intent);
//            fileListClickInterface.getPosition(i);
        });
        viewHolder.binding.tvDownload.setOnClickListener(view -> {
            createFileFolder();

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                try {
                    if (fileItem.getUri().toString().endsWith(".mp4")) {
                        fileName = "status_Saver_" + System.currentTimeMillis() + ".mp4";
                        new DownloadFileTask().execute(fileItem.getUri().toString());
                    } else {
                        fileName = "status_Saver_" + System.currentTimeMillis() + ".png";
                        new DownloadFileTask().execute(fileItem.getUri().toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                final String path = fileItem.getPath();
                String filename = path.substring(path.lastIndexOf("/") + 1);
                final File file = new File(path);
                File destFile = new File(saveFilePath);
                try {
                    FileUtils.copyFileToDirectory(file, destFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String fileNameChange = filename.substring(12);
                File newFile = new File(saveFilePath + fileNameChange);
                String contentType = "image/*";
                if (fileItem.getUri().toString().endsWith(".mp4")) {
                    contentType = "video/*";
                } else {
                    contentType = "image/*";
                }
                MediaScannerConnection.scanFile(context, new String[]{newFile.getAbsolutePath()}, new String[]{contentType},
                        new MediaScannerConnection.MediaScannerConnectionClient() {
                            public void onMediaScannerConnected() {
                                //NA
                            }

                            public void onScanCompleted(String path, Uri uri) {
                                //NA
                            }
                        });

                File from = new File(saveFilePath, filename);
                File to = new File(saveFilePath, fileNameChange);
                from.renameTo(to);
                Toast.makeText(context, context.getResources().getString(R.string.saved_to) + saveFilePath + fileNameChange, Toast.LENGTH_LONG).show();
            }

        });

        viewHolder.binding.share.setOnClickListener(view -> {
//            Log.d("check_images", "onBindViewHolder: "+fileArrayList.get(i).getFilename());
            String extension = fileArrayList.get(i).getFilename().substring(fileArrayList.get(i).getFilename().lastIndexOf("."));
            if (extension.equals(".mp4")) {
                shareVideo(context, fileArrayList.get(i).getPath());
            } else {
                shareImage(context, fileArrayList.get(i).getPath());
            }


//            if(checkPermissionREAD_EXTERNAL_STORAGE(context) == true){
//                Log.d("check_permistion", "onBindViewHolder: true");
//            getLocalBitmapUri(viewHolder.binding.pcw);
//            }else{
//                Toast.makeText(context, "false", Toast.LENGTH_SHORT).show();
//            }


//            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.setType("image/jpg");
//            final String path = fileItem.getPath();
//            String filename = path.substring(path.lastIndexOf("/") + 1);
////            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + status.getFile().getAbsolutePath()));
//            String fileNameChange = filename.substring(12);
//            File newFile = new File(saveFilePath + fileNameChange);
//            Log.d("check_files", "onBindViewHolder: "+newFile.getAbsolutePath());
////            Log.d("check_files", "onBindViewHolder: "+fileItem.getFile().getAbsolutePath());
//
//            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + fileItem.getFile().getAbsolutePath()));
//            context.startActivity(Intent.createChooser(shareIntent, "Share image"));
        });
        viewHolder.binding.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("PathVideo", fileItem.getUri().toString());
                context.startActivity(intent);

            }
        });
    }

    private void getLocalBitmapUri(ImageView couponImage) {
        // Extract Bitmap from ImageView drawable



        Drawable drawable = couponImage.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) couponImage.getDrawable()).getBitmap();
        } else {
            return;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        Log.d("previewUrl", "getLocalBitmapUri: " + bmpUri);
        try {
            Log.d("previewUrl", "getLocalBitmapUri: " + bmpUri);
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=4m65s
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.
            bmpUri = Uri.fromFile(file);

            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, "Title", null);

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            Uri imageUri = Uri.parse(path);
            share.putExtra(Intent.EXTRA_STREAM, imageUri);
            context.startActivity(Intent.createChooser(share, "Send Image Via"));

        } catch (IOException e) {
            Log.d("previewUrl", "getLocalBitmapUri: " + e);
            e.printStackTrace();
        }
        Log.d("previewUrl", "getLocalBitmapUri: " + bmpUri);
    }

    @Override
    public int getItemCount() {
        return fileArrayList == null ? 0 : fileArrayList.size();
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemsWhatsappViewBinding binding;

        public ViewHolder(ItemsWhatsappViewBinding mbinding) {
            super(mbinding.getRoot());
            this.binding = mbinding;
        }
    }

    public void initProgress() {
        dialogProgress = new ProgressDialog(context);
        dialogProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialogProgress.setTitle("Saving");
        dialogProgress.setMessage("Saving. Please wait...");
        dialogProgress.setIndeterminate(true);
        dialogProgress.setCanceledOnTouchOutside(false);
    }

    class DownloadFileTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... furl) {
            try {
                InputStream in = context.getContentResolver().openInputStream(Uri.parse(furl[0]));
                File f = null;
                f = new File(RootDirectoryWhatsappShow + File.separator + fileName);
                f.setWritable(true, false);
                OutputStream outputStream = new FileOutputStream(f);
                byte buffer[] = new byte[1024];
                int length = 0;

                while ((length = in.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.close();
                in.close();
            } catch (IOException e) {
                System.out.println("error in creating a file");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {

        }

        @Override
        protected void onPostExecute(String fileUrl) {
            Utils.setToast(context, context.getResources().getString(R.string.download_complete));
            try {
                if (Build.VERSION.SDK_INT >= 19) {
                    MediaScannerConnection.scanFile(context, new String[]
                                    {new File(RootDirectoryWhatsappShow + File.separator + fileName).getAbsolutePath()},
                            null, (path, uri) -> {
                                //no action
                            });
                } else {
                    context.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED",
                            Uri.fromFile(new File(RootDirectoryWhatsappShow + File.separator + fileName))));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
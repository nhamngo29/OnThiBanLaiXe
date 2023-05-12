package c.e.O.custom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.OnThiBangLaiXe.DBHandler;
import com.example.OnThiBangLaiXe.MainActivity;
import com.example.OnThiBangLaiXe.Model.BienBao;
import com.example.OnThiBangLaiXe.Model.CauHoi;
import com.example.OnThiBangLaiXe.Model.CauTraLoi;
import com.example.OnThiBangLaiXe.Model.DeThi;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class MyDB {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference=storage.getReference();
    DatabaseReference csdlVersion = database.getReference("Version");
    public static ProgressDialog progressDialog;
    DBHandler dbHandler;
    ValueEventListener vel;
    Context context;

    public MyDB(Context context) {
        dbHandler=new DBHandler(context);
        this.context=context;
        dbHandler=new DBHandler(context);

    }

    public void capNhatDatabase()
    {
//        DatabaseReference csdlLoaiCauHoi = database.getReference("LoaiCauHoi");
//        //Đọc loại câu hỏi
//        csdlLoaiCauHoi.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (int i = 0; i < dataSnapshot.getChildrenCount(); i++)
//                {
//                    LoaiCauHoi tlbb = dataSnapshot.child(String.valueOf(i)).getValue(LoaiCauHoi.class);
//
//                    if (tlbb != null)
//                    {
//                        if(dbHandler.findBBByID(tlbb.getMaBB()))
//                        {
//                            dbHandler.updateBB(tlbb);
//                        }
//                        else
//                        {
//                            dbHandler.insertBB(tlbb);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        DatabaseReference csdlBienBao = database.getReference("BienBao");
        Task<DataSnapshot> task = csdlBienBao.get();

        //Đọc loại câu hỏi
        csdlBienBao.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 0; i < dataSnapshot.getChildrenCount(); i++)
                {
                    BienBao tlbb = dataSnapshot.child(String.valueOf(i)).getValue(BienBao.class);

                    if (tlbb != null)
                    {
                        if(dbHandler.findBBByID(tlbb.getMaBB()))
                        {
                            dbHandler.updateBB(tlbb);
                        }
                        else
                        {
                            dbHandler.insertBB(tlbb);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        DatabaseReference csdlCauHoi = database.getReference("CauHoi");
        csdlCauHoi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < snapshot.getChildrenCount(); i++)
                {
                    CauHoi tlbb =snapshot.child(String.valueOf(i)).getValue(CauHoi.class);
                    if(tlbb != null)
                    {
                        if(dbHandler.findCHByID(tlbb.getMaCH()))
                        {
                            dbHandler.updateCauHoi(tlbb);

                        }
                        else
                        {
                            dbHandler.insertCauHoi(tlbb);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference csdlDeThi= database.getReference("DeThi");
        csdlDeThi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < snapshot.getChildrenCount(); i++)
                {

                    DeThi tlbb =snapshot.child(String.valueOf(i)).getValue(DeThi.class);
                    Log.e("DE de thi",tlbb.getMaDeThi()+"");
                    if(tlbb != null)
                    {

                        if(dbHandler.finDDeThiByID(tlbb.getMaDeThi()))
                        {
                            dbHandler.updateDeThi(tlbb);

                        }
                        else
                        {
                            dbHandler.insertDeThi(tlbb);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference csdlCauTraLoi= database.getReference("CauTraLoi");
        csdlCauTraLoi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < snapshot.getChildrenCount(); i++)
                {

                    CauTraLoi tlbb =snapshot.child(String.valueOf(i)).getValue(CauTraLoi.class);

                    if(tlbb != null)
                    {

                        if(dbHandler.findCauTraLoiByID(tlbb.getMaDeThi(),tlbb.getMaCH()))
                        {
                            dbHandler.updateCauTraLoi(tlbb);

                        }
                        else
                        {
                            dbHandler.insertCauTraLoi(tlbb);

                        }
                    }

                    if (i == snapshot.getChildrenCount() - 1)
                    {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    }
                }
                if(snapshot.getValue() != null)
                {


                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }
    public void downloadWithBytes(String type){
        StorageReference imageRefl = storageReference.child(type);
        imageRefl.listAll().addOnSuccessListener(listResult -> {
            List<StorageReference> srtList=listResult.getItems();
            for (StorageReference sr : srtList)
            {
                long SIZE=500*500;
                sr.getBytes(SIZE).addOnSuccessListener(bytes -> {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    storeImage(bitmap, sr.getName());
                    Log.e("Img",sr.getName());
                });
            }
        });
    }
    private void storeImage(Bitmap bitmap, String name) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        File file = new File(directory, name);
        if (!file.exists()) {
            Log.d("path", file.toString());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni != null && ni.isConnected()) {
            return true;
        }
        else {
            return false;
        }
    }
public boolean kiemTraPhienBan()
{
    final boolean[] isLastestVersion = {true};
    final int[] ver = {0};
    vel = csdlVersion.addValueEventListener(new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            isLastestVersion[0] = dbHandler.isLastestVersion(snapshot.getValue(int.class));
            if (!isLastestVersion[0])
            {
                Log.e("Có phiên bản mới","");
                capNhatDatabase();
                downloadWithBytes("BienBao");
                downloadWithBytes("CauHoi");
                dbHandler.UpdateVersion(snapshot.getValue(int.class));
            }
            else
            {
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }

            stop();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            isLastestVersion[0] = true;
        }
    });
    return isLastestVersion[0];
}
    private void stop()
    {
        csdlVersion.removeEventListener(vel);
    }
}

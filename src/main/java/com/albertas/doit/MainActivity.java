package com.albertas.doit;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.albertas.doit.Adapter.ListItemAdapter;
import com.albertas.doit.Model.ToDo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    List<ToDo> toDoList = new ArrayList<>();
    FirebaseFirestore db;

    RecyclerView listItem;
    RecyclerView.LayoutManager layoutManager;

    FloatingActionButton fab;

    public MaterialEditText tittle,description;
    public boolean isUpdate = false;
    public String idUpdate = "";

    ListItemAdapter adapter;

    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        dialog = new SpotsDialog(this);
        tittle = (MaterialEditText)findViewById(R.id.tittle);
        description = (MaterialEditText)findViewById(R.id.description);
        fab = (FloatingActionButton)findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isUpdate){
                    setData(tittle.getText().toString(),description.getText().toString());
                } else {
                    updateData(tittle.getText().toString(), description.getText().toString());
                    isUpdate = !isUpdate;
                }
            }
        });

        listItem = (RecyclerView)findViewById(R.id.listDoIt);
        listItem.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listItem.setLayoutManager(layoutManager);
        listItem.setAdapter(adapter);

        loadData();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("HAPUS"))
            deleteItem(item.getOrder());
        return super.onContextItemSelected(item);
    }

    private void deleteItem(int index) {
        db.collection("ToDoList")
                .document(toDoList.get(index).getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadData();
                    }
                });
    }

    private void updateData(String tittle, String description) {
        db.collection("ToDoList").document(idUpdate)
                .update("tittle",tittle,"description",description)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "SUDAH BARU", Toast.LENGTH_SHORT).show();
                    }
                });

        db.collection("ToDoList").document(idUpdate)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        loadData();
                    }
                });
    }

    private void setData(String tittle, String description) {
        //Random Id
        String id = UUID.randomUUID().toString();
        Map<String, Object> todo = new HashMap<>();
        todo.put("id",id);
        todo.put("tittle",tittle);
        todo.put("description",description);

        db.collection("ToDoList").document(id)
                .set(todo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Refresh Data
                loadData();
            }
        });

    }

    private void loadData() {
        dialog.show();
        if (toDoList.size() > 0)
            toDoList.clear();
        db.collection("ToDoList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        for(DocumentSnapshot doc:task.getResult()) {
                            ToDo toDo = new ToDo(doc.getString("id"),
                                                 doc.getString("tittle"),
                                                 doc.getString("description"));
                            toDoList.add(toDo);
                        }
                        adapter = new ListItemAdapter(MainActivity.this, toDoList);
                        listItem.setAdapter(adapter);
                        dialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
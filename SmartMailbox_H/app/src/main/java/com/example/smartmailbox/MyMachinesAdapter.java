package com.example.smartmailbox;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MyMachinesAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Machine> listMachines;

    public MyMachinesAdapter(Context context){
        this.context = context;
        listMachines = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return listMachines.size();
    }

    @Override
    public Object getItem(int position) {
        return listMachines.get(position);
    }
    public void addItems(Machine machine){
        listMachines.add(machine);
        notifyDataSetChanged();
    }


    public void setMachines(ArrayList<Machine> machines){
        listMachines = machines;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            LayoutInflater inflater =LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_machine,parent,false);
        }
        TextView machineName = convertView.findViewById(R.id.textViewName);
        TextView machineLocation = convertView.findViewById(R.id.textViewLocation);
        TextView machineDate = convertView.findViewById(R.id.textViewCreatedAt);
        ImageView editIcon = convertView.findViewById(R.id.editIcon);
        Machine mc = listMachines.get(position);
        machineName.setText(mc.getName());
        machineLocation.setText(mc.getLocation());
        machineDate.setText(mc.getCreatedAt());
        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,EditMachine.class);
                Gson gson = new Gson();
                String outstream = gson.toJson(mc);
                intent.putExtra("machine_json", outstream);
                context.startActivity(intent);

            }
        });

        return convertView;
    }
}

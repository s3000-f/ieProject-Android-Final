package ir.webgroup24.neproject.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ir.webgroup24.neproject.Activitys.ShowForm;
import ir.webgroup24.neproject.Models.Form;
import ir.webgroup24.neproject.R;

public class FormListAdapter extends RecyclerView.Adapter<FormListAdapter.QHolder> {
    private List<Form> forms;
    private Context ctx;

    public FormListAdapter(List<Form> forms, Context ctx) {
        this.forms = forms;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public QHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View listItem = layoutInflater.inflate(R.layout.form_list_item, viewGroup, false);
        return new QHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull QHolder qHolder, int i) {
        qHolder.name.setText(forms.get(i).getName());
        qHolder.itemView.setOnClickListener(v -> {
            Intent in = new Intent(ctx, ShowForm.class);
            in.putExtra("id", forms.get(i).getId());
            ctx.startActivity(in);
        });
    }

    @Override
    public int getItemCount() {
        return forms.size();
    }

    static class QHolder extends RecyclerView.ViewHolder {
        TextView name;

        QHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.list_title);
        }
    }
}

package fr.utbm.tx.quizz.javabean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import fr.utbm.tx.quizz.R;
import fr.utbm.tx.quizz.Activities.TrainingActivity;

public class CategoryAdapter extends ArrayAdapter<TrainingCategory> {

    private List<TrainingCategory> categoryList;
    private Context context;

    public CategoryAdapter(List<TrainingCategory> categoryList, Context context) {
        super(context, R.layout.listview_training, categoryList);
        this.categoryList = categoryList;
        this.context = context;
    }

    private static class CategoryHolder {
        public TextView categoryName;
        public CheckBox chkBox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        CategoryHolder holder = new CategoryHolder();

        if(convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.listview_training, null);

            holder.categoryName = (TextView) v.findViewById(R.id.category);
            holder.chkBox = (CheckBox) v.findViewById(R.id.chk_box);

            holder.chkBox.setOnCheckedChangeListener((TrainingActivity) context);

        } else {
            holder = (CategoryHolder) v.getTag();
        }

        TrainingCategory p = categoryList.get(position);
        holder.categoryName.setText(p.getName());
        holder.chkBox.setChecked(p.isSelected());
        holder.chkBox.setTag(p);

        return v;
    }
}
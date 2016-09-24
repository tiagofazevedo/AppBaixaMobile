package br.com.a3rtecnologia.baixamobile.tipo_ocorrencia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.a3rtecnologia.baixamobile.R;
import br.com.a3rtecnologia.baixamobile.tipo_documento.TipoDocumento;
import br.com.a3rtecnologia.baixamobile.tipo_ocorrencia.TipoOcorrencia;
import br.com.a3rtecnologia.baixamobile.tipo_recebedor.TipoRecebedor;

/**
 * Created by maclemon on 03/09/16.
 */
public class TipoOcorrenciaAdapter extends ArrayAdapter<TipoOcorrencia>{

    private Context context;
    private List<TipoOcorrencia> values;
    private LayoutInflater inflater;



    public TipoOcorrenciaAdapter(Context context, int textViewResourceId, List<TipoOcorrencia> values) {
        super(context, textViewResourceId, values);

        this.context = context;
        this.values = values;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    public int getCount(){
        return values.size();
    }

    public TipoOcorrencia getItem(int position){
        return values.get(position);
    }

    public long getItemId(int position){
        return position;
    }



    public int getItemIndexById(int id) {
        for (TipoOcorrencia item : values) {
            if(item.getId() == id){
                return values.indexOf(item);
            }
        }
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(R.layout.spinner, parent, false);

        TextView label = (TextView) row.findViewById(R.id.textoSpinner);

        label.setText(values.get(position).getDescricao());

        return row;
    }



    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(R.layout.spinner, parent, false);

        TextView label = (TextView) row.findViewById(R.id.textoSpinner);

        label.setText(values.get(position).getDescricao());

        return row;
    }
}


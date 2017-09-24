package innovate.jain.com.shakynote.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import innovate.jain.com.shakynote.R;
import innovate.jain.com.shakynote.activity.NoteActivity;
import innovate.jain.com.shakynote.dao.DatabaseHandler;
import innovate.jain.com.shakynote.model.Note;
import innovate.jain.com.shakynote.receviers.AlarmReceiver;
import innovate.jain.com.shakynote.utils.AlarmUtility;
import innovate.jain.com.shakynote.utils.AppUtils;

/**
 * Created by rajat on 13-11-2015
 */
public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.Holder> {

    private List<Note> notes;
    private final Context context;
    private final DatabaseHandler databaseHandler;
    private final CoordinatorLayout coordinatorLayout;

    public NoteListAdapter(List<Note> notes, Context context, CoordinatorLayout coordinatorLayout) {
        this.notes = notes;
        this.context = context;
        databaseHandler = new DatabaseHandler(context);
        this.coordinatorLayout = coordinatorLayout;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public void remove(int position) {
        if (notes != null) {
            Note note = notes.get(position);
            Intent intent = new Intent(context,
                    AlarmReceiver.class);
            AlarmUtility.alarmOff(context, intent, note.getId());
            databaseHandler.deleteNote(notes.get(position).getId());
            notes.remove(position);
        }
    }

    public List<Note> getNotes() {
        return notes;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new Holder(view, context, coordinatorLayout, this);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        holder.holder.setBackgroundColor(AppUtils.getParsedColor(notes.get(position).getColor(), context));
        holder.title.setText(notes.get(position).getTitle());
        if (notes.get(position).getType() == 0) {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(notes.get(position).getDescription());
            holder.voiceDescription.setVisibility(View.GONE);
        } else {
            holder.voiceDescription.setVisibility(View.VISIBLE);
            holder.description.setVisibility(View.GONE);
        }
        holder.delete.setTag(position);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView title;
        private final TextView description;
        private final RelativeLayout holder;
        private final Context context;
        private final ImageView delete;
        private final ImageView voiceDescription;
        private final WeakReference<NoteListAdapter> noteListAdapterWeakReference;

        public Holder(View itemView, Context context, final CoordinatorLayout coordinatorLayout,
                      final NoteListAdapter noteListAdapter) {

            super(itemView);
            holder = (RelativeLayout) itemView.findViewById(R.id.card_holder);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            delete = (ImageView) itemView.findViewById(R.id.delete);
            voiceDescription = (ImageView) itemView.findViewById(R.id.voice_description);
            this.noteListAdapterWeakReference = new WeakReference<>(noteListAdapter);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final NoteListAdapter noteListAdapter = noteListAdapterWeakReference.get();
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Note will be deleted", Snackbar.LENGTH_LONG)
                            .setAction("DELETE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    noteListAdapter.remove(getAdapterPosition());
                                    noteListAdapter.notifyItemRemoved(getAdapterPosition());

                                    Snackbar snackbar1 = Snackbar.make(coordinatorLayout, "Note deleted successfully", Snackbar.LENGTH_SHORT);
                                    snackbar1.show();
                                }
                            });

                    snackbar.show();
                }
            });

            this.context = context;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int i = getLayoutPosition();
            NoteListAdapter noteListAdapter = noteListAdapterWeakReference.get();
            Note note = noteListAdapter.getNotes().get(i);
            Intent intent = new Intent(context, NoteActivity.class);
            intent.putExtra("id", note.getId());
            context.startActivity(intent);
        }
    }

}

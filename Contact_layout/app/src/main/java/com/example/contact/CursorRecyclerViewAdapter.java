package com.example.contact;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class CursorRecyclerViewAdapter extends RecyclerView.Adapter<CursorRecyclerViewAdapter.TaskViewHolder> {
    private static final String TAG = "CursorRecyclerViewAdapt";
    private Cursor mCursor;

    public CursorRecyclerViewAdapter(Cursor cursor) {
        Log.d(TAG, "CursorRecyclerViewAdapter: Constructor called");
        mCursor = cursor;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: starts");

        if((mCursor == null) || (mCursor.getCount() == 0)) {
            Log.d(TAG, "onBindViewHolder: providing instructions");
 //           holder.name.setText("Instructions");
            holder.firstName.setText("no contacts, please add a contact.");
//            holder.editButton.setVisibility(View.GONE);
//            holder.deleteButton.setVisibility(View.GONE);
        } else {
            if(!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldn't move cursor to position " + position);
            }
            holder.firstName.setText(mCursor.getString(mCursor.getColumnIndex(ContactsContract.Columns.CONTACTS_FIRSTNAME)));
            holder.lastName.setText(mCursor.getString(mCursor.getColumnIndex(ContactsContract.Columns.CONTACTS_LASTNAME)));
//            holder.editButton.setVisibility(View.VISIBLE);  // TODO add onClick listener
//            holder.deleteButton.setVisibility(View.VISIBLE); // TODO add onClick listener
        }

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: starts");
        if((mCursor == null) || (mCursor.getCount() == 0)) {
            return 1; // fib, because we populate a single ViewHolder with instructions
        } else {
            return mCursor.getCount();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.
     * The returned old Cursor is <em>not</em> closed.
     *
     * @param newCursor The new cursor to be used
     * @return Returns the previously set Cursor, or null if there wasn't one.
     * If the given new Cursor is the same instance as the previously set
     * Cursor, null is also returned.
     */
    Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }

        final Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if(newCursor != null) {
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            // notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;

    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "TaskViewHolder";

        TextView firstName = null;
        TextView lastName = null;
//        ImageButton editButton = null;
//        ImageButton deleteButton = null;

        public TaskViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "TaskViewHolder: starts");

            this.firstName = (TextView) itemView.findViewById(R.id.cli_firstname);
            this.lastName = (TextView) itemView.findViewById(R.id.cli_lastname);
//            this.editButton = (ImageButton) itemView.findViewById(R.id.tli_edit);
//            this.deleteButton = (ImageButton) itemView.findViewById(R.id.tli_delete);
        }
    }
}


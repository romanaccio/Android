/*
 * Copyright (c) 2016.
 * Created by David Mugisha during the DTY Program
 * Last Edited on 19/01/16 18:31
 */

package com.dty.gosafe.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.dty.gosafe.model.Statements;
import com.dty.gosafe.activity.StatementView;

import java.util.List;

/**
 * Created by Dasha on 15/01/2016.
 */
public class StatementsAdapter extends ArrayAdapter<Statements> {
    /**
     * Constructor
     *
     * @param context            The current context.
     * @param resource           The resource ID for a layout file containing a layout to use when
     *                           instantiating views.
     * @param textViewResourceId The id of the TextView within the layout resource to be populated
     * @param objects            The objects to represent in the ListView.
     */
    public StatementsAdapter(Context context, int resource, int textViewResourceId, List<Statements> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     */
    public StatementsAdapter(Context context, int resource) {
        super(context, resource);
    }

    /**
     * Constructor
     *
     * @param context            The current context.
     * @param resource           The resource ID for a layout file containing a layout to use when
     *                           instantiating views.
     * @param textViewResourceId The id of the TextView within the layout resource to be populated
     */
    public StatementsAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public StatementsAdapter(Context context, int resource, Statements[] objects) {
        super(context, resource, objects);
    }

    /**
     * Constructor
     *
     * @param context            The current context.
     * @param resource           The resource ID for a layout file containing a layout to use when
     *                           instantiating views.
     * @param textViewResourceId The id of the TextView within the layout resource to be populated
     * @param objects            The objects to represent in the ListView.
     */
    public StatementsAdapter(Context context, int resource, int textViewResourceId, Statements[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public StatementsAdapter(Context context, int resource, List<Statements> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StatementView statementView;
        if (convertView instanceof StatementView) {
            statementView = (StatementView) convertView;
        } else {
            statementView = new StatementView(getContext());
        }

        Statements demo = getItem(position);

        statementView.setTitleId(demo.getLevel());
        statementView.setDescriptionId(demo.getLevel());

        Resources resources = getContext().getResources();
        String title = resources.getString(demo.getLevel());
        String description = resources.getString(demo.getLevel());
        statementView.setContentDescription(title + ". " + description);

        return statementView;
    }
}

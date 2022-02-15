package com.abc.sharefilesz.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.abc.sharefilesz.view.EditableListFragmentImpl;
import com.abc.sharefilesz.adapter.GroupEditableListFragment;
import com.abc.sharefilesz.util.IconSupport;
import com.abc.sharefilesz.model.TitleSupport;
import com.abc.sharefilesz.util.AppUtils;
import com.abc.sharefilesz.R;
import com.abc.sharefilesz.activity.ConnectionManagerActivity;
import com.abc.sharefilesz.activity.ContentSharingActivity;
import com.abc.sharefilesz.activity.ViewTransferActivity;
import com.abc.sharefilesz.adapter.TransferGroupListAdapter;
import com.abc.sharefilesz.db.AccessDatabase;
import com.abc.sharefilesz.service.CommunicationService;
import com.abc.sharefilesz.widget.GroupEditableListAdapter;
import com.genonbeta.android.database.SQLQuery;
import com.genonbeta.android.framework.widget.PowerfulActionMode;

import java.util.ArrayList;
import java.util.Map;

public class TransferGroupListFragment
        extends GroupEditableListFragment<TransferGroupListAdapter.PreloadedGroup, GroupEditableListAdapter.GroupViewHolder, TransferGroupListAdapter>
        implements IconSupport, TitleSupport
{
    private SQLQuery.Select mSelect;
    private IntentFilter mFilter = new IntentFilter();
    private BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (AccessDatabase.ACTION_DATABASE_CHANGE.equals(intent.getAction())
                    && intent.hasExtra(AccessDatabase.EXTRA_TABLE_NAME)
                    && (intent.getStringExtra(AccessDatabase.EXTRA_TABLE_NAME).equals(AccessDatabase.TABLE_TRANSFERGROUP)
                    || intent.getStringExtra(AccessDatabase.EXTRA_TABLE_NAME).equals(AccessDatabase.TABLE_TRANSFER)
            ))
                refreshList();
            else if (CommunicationService.ACTION_TASK_RUNNING_LIST_CHANGE.equals(intent.getAction())
                    && intent.hasExtra(CommunicationService.EXTRA_TASK_LIST_RUNNING)) {
                getAdapter().updateActiveList(intent.getLongArrayExtra(CommunicationService.EXTRA_TASK_LIST_RUNNING));
                refreshList();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setFilteringSupported(true);
        setDefaultOrderingCriteria(TransferGroupListAdapter.MODE_SORT_ORDER_DESCENDING);
        setDefaultSortingCriteria(TransferGroupListAdapter.MODE_SORT_BY_DATE);
        setDefaultGroupingCriteria(TransferGroupListAdapter.MODE_GROUP_BY_DATE);
        setDefaultSelectionCallback(new SelectionCallback(this));
        setUseDefaultPaddingDecoration(true);
        setUseDefaultPaddingDecorationSpaceForEdges(true);
        setDefaultPaddingDecorationSize(getResources().getDimension(R.dimen.padding_list_content_parent_layout));
    }

    @Override
    protected RecyclerView onListView(View mainContainer, ViewGroup listViewContainer)
    {
        View adaptedView = getLayoutInflater().inflate(R.layout.main, null, false);
        ((ViewGroup) mainContainer).addView(adaptedView);

        return super.onListView(mainContainer, (FrameLayout) adaptedView.findViewById(R.id.fragmentContainer));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        setEmptyImage(R.drawable.ic_compare_arrows_white_24dp);
        setEmptyText(getString(R.string.text_listEmptyTransfer));

        View viewSend = view.findViewById(R.id.sendLayoutButton);
        View viewReceive = view.findViewById(R.id.receiveLayoutButton);

        RelativeLayout send1 = (RelativeLayout) view.findViewById(R.id.send1);
        send1.setOnClickListener(new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            startActivity(new Intent(getContext(), ContentSharingActivity.class));
        }
    });

        RelativeLayout send2 = (RelativeLayout) view.findViewById(R.id.send2);
        send2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getContext(), ContentSharingActivity.class));
            }
        });

        RelativeLayout send3 = (RelativeLayout) view.findViewById(R.id.send3);
        send3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getContext(), ContentSharingActivity.class));
            }
        });

        RelativeLayout send4 = (RelativeLayout) view.findViewById(R.id.send4);
        send4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getContext(), ContentSharingActivity.class));
            }
        });


        viewSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getContext(), ContentSharingActivity.class));
            }
        });

        viewReceive.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getContext(), ConnectionManagerActivity.class)
                        .putExtra(ConnectionManagerActivity.EXTRA_ACTIVITY_SUBTITLE, getString(R.string.text_receive))
                        .putExtra(ConnectionManagerActivity.EXTRA_REQUEST_TYPE, ConnectionManagerActivity.RequestType.MAKE_ACQUAINTANCE.toString()));
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mFilter.addAction(AccessDatabase.ACTION_DATABASE_CHANGE);
        mFilter.addAction(CommunicationService.ACTION_TASK_RUNNING_LIST_CHANGE);

        if (getSelect() == null)
            setSelect(new SQLQuery.Select(AccessDatabase.TABLE_TRANSFERGROUP));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getActivity().registerReceiver(mReceiver, mFilter);

        AppUtils.startForegroundService(getActivity(), new Intent(getActivity(), CommunicationService.class)
                .setAction(CommunicationService.ACTION_REQUEST_TASK_RUNNING_LIST_CHANGE));
    }

    @Override
    public void onPause()
    {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onSortingOptions(Map<String, Integer> options)
    {
        options.put(getString(R.string.text_sortByDate), TransferGroupListAdapter.MODE_SORT_BY_DATE);
        options.put(getString(R.string.text_sortBySize), TransferGroupListAdapter.MODE_SORT_BY_SIZE);
    }

    @Override
    public void onGroupingOptions(Map<String, Integer> options)
    {
        options.put(getString(R.string.text_groupByNothing), TransferGroupListAdapter.MODE_GROUP_BY_NOTHING);
        options.put(getString(R.string.text_groupByDate), TransferGroupListAdapter.MODE_GROUP_BY_DATE);
    }

    @Override
    public TransferGroupListAdapter onAdapter()
    {
        final AppUtils.QuickActions<GroupEditableListAdapter.GroupViewHolder> quickActions = new AppUtils.QuickActions<GroupEditableListAdapter.GroupViewHolder>()
        {
            @Override
            public void onQuickActions(final GroupEditableListAdapter.GroupViewHolder clazz)
            {
                if (!clazz.isRepresentative()) {
                    registerLayoutViewClicks(clazz);

                    clazz.getView().findViewById(R.id.layout_image).setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if (getSelectionConnection() != null)
                                getSelectionConnection().setSelected(clazz.getAdapterPosition());
                        }
                    });
                }
            }
        };

        return new TransferGroupListAdapter(getActivity(), AppUtils.getDatabase(getContext()))
        {
            @NonNull
            @Override
            public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                return AppUtils.quickAction(super.onCreateViewHolder(parent, viewType), quickActions);
            }
        }.setSelect(getSelect());
    }

    @Override
    public boolean onDefaultClickAction(GroupEditableListAdapter.GroupViewHolder holder)
    {
        try {
            ViewTransferActivity.startInstance(getActivity(), getAdapter().getItem(holder).groupId);
            return true;
        } catch (Exception e) {
        }

        return false;
    }

    @Override
    public int getIconRes()
    {
        return R.drawable.ic_swap_vert_white_24dp;
    }

    @Override
    public CharSequence getTitle(Context context)
    {
        return context.getString(R.string.text_transfers);
    }

    public SQLQuery.Select getSelect()
    {
        return mSelect;
    }

    public TransferGroupListFragment setSelect(SQLQuery.Select select)
    {
        mSelect = select;
        return this;
    }

    private static class SelectionCallback extends EditableListFragment.SelectionCallback<TransferGroupListAdapter.PreloadedGroup>
    {
        public SelectionCallback(EditableListFragmentImpl<TransferGroupListAdapter.PreloadedGroup> fragment)
        {
            super(fragment);
        }

        @Override
        public boolean onPrepareActionMenu(Context context, PowerfulActionMode actionMode)
        {
            super.onPrepareActionMenu(context, actionMode);
            return true;
        }

        @Override
        public boolean onCreateActionMenu(Context context, PowerfulActionMode actionMode, Menu menu)
        {
            super.onCreateActionMenu(context, actionMode, menu);
            actionMode.getMenuInflater().inflate(R.menu.action_mode_group, menu);
            return true;
        }

        @Override
        public boolean onActionMenuItemSelected(Context context, PowerfulActionMode actionMode, MenuItem item)
        {
            int id = item.getItemId();

            ArrayList<TransferGroupListAdapter.PreloadedGroup> selectionList = new ArrayList<>(getFragment().getSelectionConnection().getSelectedItemList());

            if (id == R.id.action_mode_group_delete)
                AppUtils.getDatabase(getFragment().getContext())
                        .removeAsynchronous(getFragment().getActivity(), selectionList);
            else if (id == R.id.action_mode_group_serve_on_web
                    || id == R.id.action_mode_group_hide_on_web) {
                boolean success = false;

                for (TransferGroupListAdapter.PreloadedGroup group : selectionList) {
                    group.isServedOnWeb = group.index.outgoingCount > 0
                            && id == R.id.action_mode_group_serve_on_web;

                    if (group.isServedOnWeb)
                        success = true;
                }

                AppUtils.getDatabase(getFragment().getContext()).update(selectionList);

                if (success)
                    AppUtils.startWebShareActivity(getFragment().getActivity(), true);
            } else
                return super.onActionMenuItemSelected(context, actionMode, item);

            return true;
        }
    }
}
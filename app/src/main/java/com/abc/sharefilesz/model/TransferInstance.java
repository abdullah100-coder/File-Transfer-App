package com.abc.sharefilesz.model;

import com.abc.sharefilesz.db.AccessDatabase;
import com.abc.sharefilesz.util.NetworkDeviceLoader;

public class TransferInstance
{
    private NetworkDevice mDevice;
    private TransferGroup mGroup;
    private NetworkDevice.Connection mConnection;
    private TransferGroup.Assignee mAssignee;

    public TransferInstance(AccessDatabase database, long groupId, String using, boolean findDevice) throws Exception
    {
        buildAll(database, groupId, using, findDevice);
    }

    private TransferInstance()
    {

    }

    protected void buildAll(AccessDatabase database, long groupId, String using, boolean findDevice) throws Exception
    {
        buildGroup(database, groupId);

        if (findDevice) {
            buildDevice(database, using);
            buildAssignee(database, mGroup, mDevice);
            buildConnection(database, mAssignee);
        } else {
            buildConnection(database, using);
            buildDevice(database, mConnection.deviceId);
            buildAssignee(database, mGroup, mDevice);
        }

        NetworkDeviceLoader.processConnection(database, getDevice(), getConnection());

        if (!getAssignee().connectionAdapter.equals(getConnection().adapterName)) {
            getAssignee().connectionAdapter = getConnection().adapterName;
            database.publish(getAssignee());
        }
    }

    protected void buildAssignee(AccessDatabase database, TransferGroup group, NetworkDevice device) throws Exception
    {
        if (mAssignee != null)
            return;

        try {
            TransferGroup.Assignee assignee = new TransferGroup.Assignee(group, device);

            database.reconstruct(assignee);

            mAssignee = assignee;
        } catch (Exception e) {
            throw new Exception();
        }
    }

    protected void buildConnection(AccessDatabase database, String connectionAddress) throws Exception
    {
        if (mConnection != null)
            return;

        try {
            NetworkDevice.Connection connection = new NetworkDevice.Connection(connectionAddress);

            database.reconstruct(connection);

            mConnection = connection;
        } catch (Exception e) {
            throw new Exception();
        }
    }

    protected void buildConnection(AccessDatabase database, TransferGroup.Assignee assignee) throws Exception
    {
        if (mConnection != null)
            return;

        try {
            NetworkDevice.Connection connection = new NetworkDevice.Connection(assignee);

            database.reconstruct(connection);

            mConnection = connection;
        } catch (Exception e) {
            throw new Exception();
        }
    }

    protected void buildDevice(AccessDatabase database, String deviceId) throws Exception
    {
        if (mDevice != null)
            return;

        try {
            NetworkDevice device = new NetworkDevice(deviceId);

            database.reconstruct(device);

            mDevice = device;
        } catch (Exception e) {
            throw new Exception();
        }
    }

    protected void buildGroup(AccessDatabase database, long groupId) throws Exception
    {
        if (mGroup != null)
            return;

        try {
            TransferGroup group = new TransferGroup(groupId);

            database.reconstruct(group);

            mGroup = group;
        } catch (Exception e) {
            throw new Exception();
        }
    }


    public TransferGroup.Assignee getAssignee()
    {
        return mAssignee;
    }

    public NetworkDevice.Connection getConnection()
    {
        return mConnection;
    }

    public NetworkDevice getDevice()
    {
        return mDevice;
    }

    public TransferGroup getGroup()
    {
        return mGroup;
    }

    public static class Builder
    {
        private TransferInstance mTransferInstance = new TransferInstance();

        public TransferInstance build(AccessDatabase database, long groupId, String using, boolean findDevice) throws Exception, Exception, Exception, Exception
        {
            mTransferInstance.buildAll(database, groupId, using, findDevice);
            return mTransferInstance;
        }

        public Builder supply(TransferGroup group)
        {
            mTransferInstance.mGroup = group;
            return this;
        }

        public Builder supply(NetworkDevice device)
        {
            mTransferInstance.mDevice = device;
            return this;
        }

        public Builder supply(NetworkDevice.Connection connection)
        {
            mTransferInstance.mConnection = connection;
            return this;
        }

        public Builder supply(TransferGroup.Assignee assignee)
        {
            mTransferInstance.mAssignee = assignee;
            return this;
        }
    }
}

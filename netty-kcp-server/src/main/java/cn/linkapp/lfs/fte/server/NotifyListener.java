package cn.linkapp.lfs.fte.server;

import cn.linkapp.lfs.fte.Datagram;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * 文件传输完成时通知服务端
 */
public class NotifyListener implements PropertyChangeListener {
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Datagram.Info info = (Datagram.Info)evt.getNewValue();
        if (evt.getPropertyName().equals(NettyKcpServerHandler.Event.LAST_PACKAGE.getName())) {
            System.out.println("[NotifyListener] file done: id=" + info.getCrc());
        }
    }
}

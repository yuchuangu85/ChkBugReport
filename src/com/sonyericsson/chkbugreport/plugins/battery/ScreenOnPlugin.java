package com.sonyericsson.chkbugreport.plugins.battery;

import com.sonyericsson.chkbugreport.Module;
import com.sonyericsson.chkbugreport.plugins.logs.LogLine;
import com.sonyericsson.chkbugreport.plugins.logs.event.EventLogPlugin;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Vector;

public class ScreenOnPlugin extends ChartPlugin {

    private Vector<LogLine> mEventLog;

    @Override
    public int getType() {
        return TYPE_STRIP;
    }

    @Override
    public String getName() {
        return "screen";
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Module mod) {
        mEventLog = (Vector<LogLine>) mod.getInfo(EventLogPlugin.INFO_ID_LOG);
        if (mEventLog != null) {
            if (mEventLog.size() > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(Graphics2D g, int x, int y, int w, int h, long firstTs, long lastTs) {
        int lastX = x;
        int lastMode = -1;
        Color cols[] = {
                new Color(0x4080ff80, true),
                new Color(0x80ffff80, true),
                new Color(0xffff8080, true),
        };
        long duration = lastTs - firstTs;
        for (LogLine l : mEventLog) {
            if (!"screen_toggled".equals(l.tag)) continue;
            int mode = Integer.parseInt(l.msg);
            if (lastMode == -1) {
                lastMode = (mode == 0) ? 2 : 0;
            }
            int cx = (int) (x + (l.ts - firstTs) * w / duration);
            g.setColor(cols[lastMode]);
            g.fillRect(lastX, y, cx - lastX + 1, h);
            lastX = cx;
            lastMode = mode;
        }
        if (lastMode >= 0) {
            g.setColor(cols[lastMode]);
            g.fillRect(lastX, y, x + w - lastX, h);
        }
    }

}

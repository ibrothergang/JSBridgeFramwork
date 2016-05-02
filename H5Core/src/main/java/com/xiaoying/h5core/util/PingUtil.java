package com.xiaoying.h5core.util;

import android.os.Looper;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PingUtil {

    private static final boolean DBG = true;
    private static final String TAG = "PingUtil";

    /**
     * ping a target host one time
     * NOTE: the function shouldn't be invoked from MainThread
     *
     * @param host the target host to be "ping"ed
     * @return PingResult
     */
    public static PingResult ping(final String host) {
        log("ping() host:" + host);
        if (Looper.myLooper() != null && Looper.getMainLooper().equals(Looper.myLooper())) {
            throw new IllegalThreadStateException("ping shouldn't be invoked in MainThread!");
        }
        PingResult result = new PingResult();
        if (host == null || TextUtils.isEmpty(host)) {
            return result;
        }
        Runtime runtime = Runtime.getRuntime();
        try {
            Process pingProcess = runtime.exec("/system/bin/ping -w 1 -c 1 " + host);

            int count = 0;
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    pingProcess.getInputStream()));

            StringBuffer output = new StringBuffer();
            String temp;

            while ((temp = reader.readLine()) != null)// .read(buffer)) > 0)
            {
                output.append(temp + "\n");
                count++;
            }

            reader.close();

            String pingOutPut = null;
            if (count > 0) {
                pingOutPut = output.toString();
                Pattern re = Pattern.compile("^PING\\b" //
                                + "[^(]*\\(([^)]*)\\)" // #capture IP
                                + "\\s([^.]*)\\." // # capture the bytes of data
                                + ".*?^(\\d+\\sbytes)" // # capture bytes
                                + ".*?icmp_seq=(\\d+)" // # capture icmp_seq
                                + ".*?ttl=(\\d+)" // # capture ttl
                                + ".*?time=(.*?)ms" // # capture time
                                + ".*?(\\d+)\\spackets\\stransmitted" //
                                + ".*?(\\d+)\\sreceived" //
                                + ".*?(\\d+%)\\spacket\\sloss" //
                                + ".*?time\\s(\\d+ms)" //
                                + ".*?=\\s([^\\/]*)\\/([^\\/]*)\\/([^\\/]*)\\/(.*?)\\sms",
                        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
                Matcher m = re.matcher(pingOutPut);
                int mIdx = 0;
                while (m.find()) {
                    for (int groupIdx = 0; groupIdx < m.groupCount() + 1; groupIdx++) {
                        log("regex[" + mIdx + "][" + groupIdx + "] = "
                                + m.group(groupIdx));
                    }
                    try {
                        result.ipAddr = m.group(1);
                        result.consumedTimeMs = Float.valueOf(m.group(6).trim())
                                .floatValue();
                        result.numSendPkt = Integer.valueOf(m.group(7));
                        result.numReceivedPkt = Integer.valueOf(m.group(8));
                        result.loss = m.group(9);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        return result;
                    }

                    mIdx++;
                }
            }

            int mExitValue = pingProcess.waitFor();
            log("mExitValue " + mExitValue);
            if (mExitValue != 0) {
                result.consumedTimeMs = -1;
                return result;
            }
        } catch (InterruptedException ignore) {
            ignore.printStackTrace();
            log("Exception:" + ignore);
        } catch (IOException e) {
            e.printStackTrace();
            log("Exception:" + e);
        }
        return result;
    }

    private static void log(String string) {
        // TODO Auto-generated method stub
        if (DBG && !TextUtils.isEmpty(string)) {
            H5Log.d(TAG, "" + string);
        }
    }

    public static final class PingResult {
        public String ipAddr;
        public float consumedTimeMs;
        public int numSendPkt;
        public int numReceivedPkt;
        public String loss;

        public PingResult() {
            consumedTimeMs = -1;
        }

        @Override
        public String toString() {
            return "PingResult\n\n target IP:" + ipAddr + "\nconsumed:" + consumedTimeMs
                    + "ms\nnumber of packet(s) sent:" + numSendPkt
                    + "\nnumber of package(s) received:" + numReceivedPkt
                    + "\nloss:" + loss + "\n";
        }

        public boolean success() {
            return consumedTimeMs != -1;
        }
    }

}

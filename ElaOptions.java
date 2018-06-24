package com.caihang.ylyim.ela;

import org.elastos.carrier.Carrier;
import org.elastos.carrier.Carrier.Options;

import java.io.File;
import java.util.ArrayList;

public class ElaOptions extends Carrier.Options {
    public ElaOptions(String path) {
        super();
        File file = new File(path);
        if (file.exists())
            file.delete();
        file.mkdir();
        try {
            setUdpEnabled(true);
            setPersistentLocation(path);
            ArrayList<BootstrapNode> arrayList = new ArrayList<>();
            BootstrapNode node = new BootstrapNode();
            node.setIpv4("13.58.208.50");
            node.setPort("33445");
            node.setPublicKey("89vny8MrKdDKs7Uta9RdVmspPjnRMdwMmaiEW27pZ7gh");
            arrayList.add(node);

            node = new BootstrapNode();
            node.setIpv4("18.216.102.47");
            node.setPort("33445");
            node.setPublicKey("G5z8MqiNDFTadFUPfMdYsYtkUDbX5mNCMVHMZtsCnFeb");
            arrayList.add(node);

            node = new BootstrapNode();
            node.setIpv4("18.216.6.197");
            node.setPort("33445");
            node.setPublicKey("H8sqhRrQuJZ6iLtP2wanxt4LzdNrN2NNFnpPdq1uJ9n2");
            arrayList.add(node);

            node = new BootstrapNode();
            node.setIpv4("54.223.36.193");
            node.setPort("33445");
            node.setPublicKey("5tuHgK1Q4CYf4K5PutsEPK5E3Z7cbtEBdx7LwmdzqXHL");
            arrayList.add(node);

            node = new BootstrapNode();
            node.setIpv4("52.80.187.125");
            node.setPort("33445");
            node.setPublicKey("3khtxZo89SBScAMaHhTvD68pPHiKxgZT6hTCSZZVgNEm");
            arrayList.add(node);

            setBootstrapNodes(arrayList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

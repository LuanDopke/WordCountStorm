package org.example;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.thrift.TException;
import org.apache.storm.topology.TopologyBuilder;

public class Main {
    public static void main(String[] args) {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("spout", new BookSpout());
        builder.setBolt("split", new SplitBolt()).shuffleGrouping("spout");
        builder.setBolt("count", new CountBolt()).shuffleGrouping("split");
        builder.setBolt("sink", new FileSink()).shuffleGrouping("count");
        Config conf = new Config();
        //conf.setDebug(true);

        LocalCluster cluster = null;
        try {
            cluster = new LocalCluster();
            cluster.submitTopology("word-count", conf, builder.createTopology());
            Thread.sleep(30000);
        } catch (TException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            cluster.shutdown();
        }

    }
}
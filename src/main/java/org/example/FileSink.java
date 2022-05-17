package org.example;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class FileSink extends BaseRichBolt {
    protected long lastTime;
    protected long count = 0;
    private HashMap<String, Integer> counts = null;

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        this.counts = new HashMap<String, Integer>();

    }

    @Override
    public void execute(Tuple input) {
        String word = input.getStringByField("word");
        Integer count = input.getIntegerByField("count");
        counts.put(word, count);
        calculateThroughput(Instant.now().getEpochSecond());
    }

    @Override
    public void cleanup() {
        super.cleanup();

        counts.forEach((s, count) -> {
            try {
                Files.write(Paths.get("/home/luan/Documents/repositorio/WordCountStorm/src/main/data/result.dat"),
                        (s + ", " + count + "\n").getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void calculateThroughput(long unixTime) {
        if (this.lastTime == 0)
            this.lastTime = unixTime;

        if (this.lastTime == unixTime) {
            count++;
        } else {
            try (PrintWriter pw = new PrintWriter(new FileWriter("/home/luan/Documents/repositorio/WordCountStorm/src/main/data/Throughput/" + this.getClass().getSimpleName() + ".csv", true), true)) {
                pw.println(lastTime + "," + count);
                this.count = 1;
                this.lastTime = unixTime;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

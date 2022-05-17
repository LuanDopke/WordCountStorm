package org.example;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class CountBolt extends BaseBasicBolt {
    Map<String, Integer> counts = new HashMap<>();
    protected long lastTime;
    protected long count = 0;

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String word = tuple.getString(0);
        Integer count = counts.get(word);
        if (count == null)
            count = 0;
        count++;
        counts.put(word, count);
        collector.emit(new Values(word, count));
        calculateThroughput(Instant.now().getEpochSecond());
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

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word", "count"));
    }
}

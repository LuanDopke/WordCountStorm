package org.example;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.io.*;
import java.time.Instant;
import java.util.Map;
import java.util.Scanner;

public class BookSpout extends BaseRichSpout {
    SpoutOutputCollector spoutOutputCollector;

    protected File file = new File("/home/luan/Documents/repositorio/WordCountStorm/src/main/data/books.dat");
    protected Scanner scanner;
    protected long lastTime;
    protected long count = 0;

    @Override
    public void open(Map<String, Object> conf, TopologyContext context, SpoutOutputCollector collector) {
        this.spoutOutputCollector = collector;
    }

    @Override
    public void nextTuple() {
        if (scanner == null) {
            try {
                scanner = new Scanner(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        if (scanner.hasNextLine()) {
            spoutOutputCollector.emit(new Values(scanner.nextLine()));
        }

        //new Thread(() -> { precisa ser async?
        calculateThroughput(Instant.now().getEpochSecond());
        // }).start();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("frase"));
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

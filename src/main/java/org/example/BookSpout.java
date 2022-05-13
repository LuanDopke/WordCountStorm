package org.example;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

public class BookSpout extends BaseRichSpout {
    SpoutOutputCollector spoutOutputCollector;

    protected File file = new File("/home/luan/Documents/repositorio/WordCountStorm/src/main/data/books.dat");
    protected Scanner scanner;
    protected int curFileIndex = 0;
    protected int curLineIndex = 0;
    private final boolean finished = false;

    protected int taskId;
    protected int numTasks;

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
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("frase"));
    }
}

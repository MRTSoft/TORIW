package gombak;

public class Result {
    public String Record;
    public Double Score;

    public Result (String record, Double score){
        Record = record;
        Score = score;
    }

    public Result() {
        Record = "";
        Score = 0.0;
    }
}

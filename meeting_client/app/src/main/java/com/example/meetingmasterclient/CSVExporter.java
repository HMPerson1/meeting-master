package com.example.meetingmasterclient;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class CSVExporter {
    private String subject;
    private String startDate;
    private String startTime;
    private String endTime;
    private String location;
    private String desc;

    public CSVExporter(){
        this.subject = "";
        this.startDate = "";
        this.startTime = "";
        this.endTime = "";
        this.location = "";
        this.desc = "";
    }

    public void setSubject(String subject){
        this.subject = subject;
    }

    public void setStartDate(String startDate){
        this.startDate = startDate;
    }

    public void setStartTime(String startTime){
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setEvent(String subject, String startDate, String startTime, String location){
        this.subject = subject;
        this.startDate = startDate;
        this.startTime = startTime;
        this.location = location;
    }

    public void writeToFile(String fileName){
        File f = new File(fileName);
        try (PrintWriter pw = new PrintWriter(f)){
            StringBuilder sb = new StringBuilder();

            //define columns
            sb.append("Subject,");
            sb.append("Start Date,");
            sb.append("All Day Event,");
            sb.append("Start Time,");
            sb.append("End Time,");
            sb.append("Location,");
            sb.append("Description,");

            //write details to StringBuilder
            sb.append(subject);
            sb.append(startDate);
            sb.append("FALSE");
            sb.append(startTime);
            sb.append(endTime);
            sb.append(location);
            sb.append(desc);
            
            //write StringBuilder to file
            pw.write(sb.toString());

        } catch (FileNotFoundException fe){
            System.out.println(fe.getMessage());
        }
    }
}

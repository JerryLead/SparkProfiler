package profiler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

import appinfo.Job;
import appinfo.Stage;
import appinfo.Task;
import jxl.write.WriteException;
import parser.JobJsonParser;
import parser.SingleJobJsonParser;
import parser.StageJsonParser;
import parser.TaskJsonParser;
import util.JxlUtil;

/**
 * Created by Ye on 2017/2/14.
 * Modified by YE on 2017/3/20.
 */

public class SparkAppProfiler {

    public static void main(String args[]) throws IOException, WriteException {

        String masterIP = "";
        String appIdsFile = ""; // put the appIds to be profiled
        String outputDir = "";

        List<String> appIdList = new ArrayList<String>();
        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(appIdsFile));

            String appIdStr;
            int m = 0;

            while ((appIdStr = br.readLine()) != null) {
                if (appIdStr != "\r") {
                    appIdList.add(appIdStr.trim());
                    m++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        for (String appid : appIdList) {

            String jobPath = "E:\\ali\\" + appid + "Job.xls";//声明需要的存放的数据位置
            String stagePath = "E:\\ali\\" + appid + "Stage.xls";
            String taskPath = "E:\\ali\\" + appid + "Task.xls";
            String jobnotstagePath = "E:\\ali\\" + appid + "JobNotStage.xls";

            JxlUtil ju1 = new JxlUtil();
            ju1.setPath(jobPath);

            JxlUtil ju2 = new JxlUtil();
            ju2.setPath(stagePath);

            JxlUtil ju3 = new JxlUtil();
            ju3.setPath(taskPath);

            JxlUtil ju4 = new JxlUtil();
            ju4.setPath(jobnotstagePath);


            JobJsonParser jobParser = new JobJsonParser();
            jobParser.setJobUrl(masterIP, appid);
            jobParser.jobwsu();
            List<Job> job = jobParser.getJobList();

            Map<String, List<List<String>>> listListMap1 = new HashMap<String, List<List<String>>>();
            List<List<String>> listList1 = new ArrayList<List<String>>();
            List<String> list1 = new ArrayList<String>();
            list1.add("JobID");
            list1.add("JobDescription");
            list1.add("StageID");
            list1.add("SubmitTime");
            list1.add("CompleteTime");
            list1.add("JobDuration/s");
            listList1.add(list1);

            for (int i = 0; i < job.size(); i++) {
                List<String> list11 = new ArrayList<String>();
                list11.add(job.get(i).getJobID());
                list11.add(job.get(i).getName());
                list11.add(job.get(i).getStageID());
                list11.add(job.get(i).getSubmitTime());
                list11.add(job.get(i).getCompleteTime());
                list11.add(job.get(i).getDuration());
                listList1.add(list11);
            }

            listListMap1.put("Job History", listList1);
            ju1.write(listListMap1);

            StageJsonParser read2 = new StageJsonParser();
            read2.setStageUrl(appid);
            read2.stagewsu();
            List<Stage> stage = read2.getStageList();

            Map<String, List<List<String>>> listListMap2 = new HashMap<String, List<List<String>>>();
            List<List<String>> listList2 = new ArrayList<List<String>>();
            List<String> list2 = new ArrayList<String>();
            list2.add("StageID");
            list2.add("StageDescription");
            list2.add("SubmitTime");
            list2.add("firstTaskSubmit");
            list2.add("CompleteTime");
            list2.add("StageDuration/s");
            list2.add("numCompleteTasks");
            list2.add("attemptID");
            list2.add("status");

            listList2.add(list2);


            for (int j = 0; j < stage.size(); j++) {
                List<String> list22 = new ArrayList<String>();
                list22.add(stage.get(j).getStageId());
                list22.add(stage.get(j).getName());
                list22.add(stage.get(j).getSubmitTime());
                list22.add(stage.get(j).getFirstTaskSubmit());
                list22.add(stage.get(j).getCompleteTime());
                list22.add(stage.get(j).getDuration());
                list22.add(stage.get(j).getNumCompleteTasks());
                list22.add(stage.get(j).getAttemptId());
                //  System.out.println(stage.get(j).getAttemptId());
                list22.add(stage.get(j).getStatus());

                listList2.add(list22);
            }
            listListMap2.put("Stage History", listList2);
            ju2.write(listListMap2);

            TaskJsonParser read3 = new TaskJsonParser();
            for (int i = 0; i < stage.size(); i++) {
                System.out.println(stage.get(i).getTaskUrl());
                read3.setTaskUrl(stage.get(i).getTaskUrl());
                read3.setStageId(stage.get(i).getStageId());
                read3.setStageStatus(stage.get(i).getStatus());
                read3.taskwsu();
            }

            List<Task> task = read3.getTaskList();

            Map<String, List<List<String>>> listListMap3 = new HashMap<String, List<List<String>>>();
            List<List<String>> listList3 = new ArrayList<List<String>>();
            List<String> list3 = new ArrayList<String>();
            list3.add("TaskID");
            list3.add("TaskStatus");
            list3.add("StageID");
            list3.add("ExecutorID");
            list3.add("GC Time/ms");
            list3.add("Duration/ms");
            list3.add("stageStatus");
            //   list1.add("StageID");
            listList3.add(list3);
            for (int i = 0; i < task.size(); i++) {
                List<String> list33 = new ArrayList<String>();
                list33.add(task.get(i).getTaskId());
                list33.add(task.get(i).getTaskStatus());
                list33.add(task.get(i).getStageId());
                list33.add(task.get(i).getExecutorId());
                list33.add(task.get(i).getJvmGcTime());
                list33.add(task.get(i).getExecutorRunTime());
                list33.add(task.get(i).getStageStatus());

                listList3.add(list33);
            }

            listListMap3.put("Job History", listList3);
            ju3.write(listListMap3);

            SingleJobJsonParser read4 = new SingleJobJsonParser();
            read4.setJobUrl(masterIP, appid);
            read4.jobwsu();
            List<Job> job1 = read4.getJobList();

            Map<String, List<List<String>>> listListMap4 = new HashMap<String, List<List<String>>>();
            List<List<String>> listList4 = new ArrayList<List<String>>();
            List<String> list4 = new ArrayList<String>();
            list4.add("JobID");
            list4.add("JobDescription");
            //list1.add("StageID");
            list4.add("SubmitTime");
            list4.add("CompleteTime");
            list4.add("JobDuration/s");
            listList4.add(list4);

            for (int i = 0; i < job1.size(); i++) {
                List<String> list44 = new ArrayList<String>();
                list44.add(job1.get(i).getJobID());
                list44.add(job1.get(i).getName());
                list44.add(job1.get(i).getSubmitTime());
                list44.add(job1.get(i).getCompleteTime());
                list44.add(job1.get(i).getDuration());
                listList4.add(list44);
            }

            listListMap4.put("Job History", listList4);
            ju4.write(listListMap4);

        }
    }
}

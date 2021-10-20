package com.ys.springbatch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

//여기서 잡을 실행
@RequiredArgsConstructor
public class JobRunner implements ApplicationRunner {

    private JobLauncher jobLauncher;

    private Job job; // 빈으로 주입

    @Override
    public void run(ApplicationArguments args) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder().addString("name", "user2")
                .toJobParameters();

        jobLauncher.run(job, jobParameters);
    }

}

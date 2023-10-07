package com.usth.jobadmin.model

data class JobStatus(
    var student: Student = Student(),
    var jobApplication: JobApplication = JobApplication()
)

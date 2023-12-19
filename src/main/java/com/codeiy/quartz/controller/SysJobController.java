/*
 *    Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the pig4cloud.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: lengleng (wangiegie@gmail.com)
 */

package com.codeiy.quartz.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codeiy.common.log.AutoLog;
import com.codeiy.common.util.R;
import com.codeiy.common.util.SecurityUtils;
import com.codeiy.quartz.constants.QuartzEnum;
import com.codeiy.quartz.entity.SysJob;
import com.codeiy.quartz.entity.SysJobLog;
import com.codeiy.quartz.service.SysJobLogService;
import com.codeiy.quartz.service.SysJobService;
import com.codeiy.quartz.util.TaskUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.quartz.Scheduler;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author frwcloud
 * @date 2019-01-27 10:04:42
 * <p>
 * 定时任务管理
 */
@RestController
@AllArgsConstructor
@RequestMapping("/sys-job")
@Tag(description = "sys-job", name = "定时任务")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysJobController {

	private final SysJobService sysJobService;

	private final SysJobLogService sysJobLogService;

	private final TaskUtil taskUtil;

	private final Scheduler scheduler;

	/**
	 * 定时任务分页查询
	 * @param page 分页对象
	 * @param sysJob 定时任务调度表
	 * @return
	 */
	@GetMapping("/page")
	@Operation(description = "分页定时业务查询")
	public R getSysJobPage(Page page, SysJob sysJob) {
		LambdaQueryWrapper<SysJob> wrapper = Wrappers.<SysJob>lambdaQuery()
			.like(StrUtil.isNotBlank(sysJob.getJobName()), SysJob::getJobName, sysJob.getJobName())
			.like(StrUtil.isNotBlank(sysJob.getJobGroup()), SysJob::getJobGroup, sysJob.getJobGroup())
			.eq(StrUtil.isNotBlank(sysJob.getJobStatus()), SysJob::getJobStatus, sysJob.getJobGroup())
			.eq(StrUtil.isNotBlank(sysJob.getJobExecuteStatus()), SysJob::getJobExecuteStatus,
					sysJob.getJobExecuteStatus());
		return R.ok(sysJobService.page(page, wrapper));
	}

	/**
	 * 通过id查询定时任务
	 * @param id id
	 * @return R
	 */
	@GetMapping("/{id}")
	@Operation(description = "唯一标识查询定时任务")
	public R getById(@PathVariable("id") Long id) {
		return R.ok(sysJobService.getById(id));
	}

	/**
	 * 新增定时任务
	 * @param sysJob 定时任务调度表
	 * @return R
	 */
	@AutoLog("新增定时任务")
	@PostMapping
	@Operation(description = "新增定时任务")
	public R save(@RequestBody SysJob sysJob) {
		sysJob.setJobStatus(QuartzEnum.JOB_STATUS_RELEASE.getType());
		sysJob.setCreateBy(SecurityUtils.getUsername());
		sysJobService.save(sysJob);
		// 初始化任务
		taskUtil.addOrUpateJob(sysJob, scheduler);
		return R.ok();
	}

	/**
	 * 修改定时任务
	 * @param sysJob 定时任务调度表
	 * @return R
	 */
	@AutoLog("修改定时任务")
	@PutMapping
	@Operation(description = "修改定时任务")
	public R updateById(@RequestBody SysJob sysJob) {
		sysJob.setUpdateBy(SecurityUtils.getUsername());
		SysJob querySysJob = this.sysJobService.getById(sysJob.getJobId());
		if (QuartzEnum.JOB_STATUS_NOT_RUNNING.getType().equals(querySysJob.getJobStatus())) {
			this.taskUtil.addOrUpateJob(sysJob, scheduler);
			sysJobService.updateById(sysJob);
		}
		else if (QuartzEnum.JOB_STATUS_RELEASE.getType().equals(querySysJob.getJobStatus())) {
			sysJobService.updateById(sysJob);
		}
		return R.ok();
	}

	/**
	 * 通过id删除定时任务
	 * @param id id
	 * @return R
	 */
	@AutoLog("删除定时任务")
	@DeleteMapping("/{id}")
	@Operation(description = "唯一标识查询定时任务，暂停任务才能删除")
	public R removeById(@PathVariable Long id) {
		SysJob querySysJob = this.sysJobService.getById(id);
		if (QuartzEnum.JOB_STATUS_NOT_RUNNING.getType().equals(querySysJob.getJobStatus())) {
			this.taskUtil.removeJob(querySysJob, scheduler);
			this.sysJobService.removeById(id);
		}
		else if (QuartzEnum.JOB_STATUS_RELEASE.getType().equals(querySysJob.getJobStatus())) {
			this.sysJobService.removeById(id);
		}
		return R.ok();
	}

	/**
	 * 暂停全部定时任务
	 * @return
	 */
	@AutoLog("暂停全部定时任务")
	@PostMapping("/shutdown-jobs")
	@Operation(description = "暂停全部定时任务")
	public R shutdownJobs() {
		taskUtil.pauseJobs(scheduler);
		long count = this.sysJobService.count(
				new LambdaQueryWrapper<SysJob>().eq(SysJob::getJobStatus, QuartzEnum.JOB_STATUS_RUNNING.getType()));
		if (count <= 0) {
			return R.ok("无正在运行定时任务");
		}
		else {
			// 更新定时任务状态条件，运行状态2更新为暂停状态2
			this.sysJobService.update(
					SysJob.builder().jobStatus(QuartzEnum.JOB_STATUS_NOT_RUNNING.getType()).build(),
					new UpdateWrapper<SysJob>().lambda()
						.eq(SysJob::getJobStatus, QuartzEnum.JOB_STATUS_RUNNING.getType()));
			return R.ok("暂停成功");
		}
	}

	/**
	 * 启动全部定时任务
	 * @return
	 */
	@AutoLog("启动全部定时任务")
	@PostMapping("/start-jobs")
	@Operation(description = "启动全部定时任务")
	public R startJobs() {
		// 更新定时任务状态条件，暂停状态3更新为运行状态2
		this.sysJobService.update(SysJob.builder().jobStatus(QuartzEnum.JOB_STATUS_RUNNING.getType()).build(),
				new UpdateWrapper<SysJob>().lambda()
					.eq(SysJob::getJobStatus, QuartzEnum.JOB_STATUS_NOT_RUNNING.getType()));
		taskUtil.startJobs(scheduler);
		return R.ok();
	}

	/**
	 * 刷新全部定时任务
	 * @return
	 */
	@AutoLog("刷新全部定时任务")
	@PostMapping("/refresh-jobs")
	@Operation(description = "刷新全部定时任务")
	public R refreshJobs() {
		sysJobService.list().forEach((sysjob) -> {
			if (QuartzEnum.JOB_STATUS_RELEASE.getType().equals(sysjob.getJobStatus())
					|| QuartzEnum.JOB_STATUS_DEL.getType().equals(sysjob.getJobStatus())) {
				taskUtil.removeJob(sysjob, scheduler);
			}
			else if (QuartzEnum.JOB_STATUS_RUNNING.getType().equals(sysjob.getJobStatus())
					|| QuartzEnum.JOB_STATUS_NOT_RUNNING.getType().equals(sysjob.getJobStatus())) {
				taskUtil.addOrUpateJob(sysjob, scheduler);
			}
			else {
				taskUtil.removeJob(sysjob, scheduler);
			}
		});
		return R.ok();
	}

	/**
	 * 启动定时任务
	 * @param jobId
	 * @return
	 */
	@AutoLog("启动定时任务")
	@PostMapping("/start-job/{id}")
	@Operation(description = "启动定时任务")
	public R startJob(@PathVariable("id") Long jobId) {
		SysJob querySysJob = this.sysJobService.getById(jobId);
		if (querySysJob != null && QuartzEnum.JOB_LOG_STATUS_FAIL.getType().equals(querySysJob.getJobStatus())) {
			taskUtil.addOrUpateJob(querySysJob, scheduler);
		}
		else {
			taskUtil.resumeJob(querySysJob, scheduler);
		}
		// 更新定时任务状态条件，暂停状态3更新为运行状态2
		this.sysJobService
			.updateById(SysJob.builder().jobId(jobId).jobStatus(QuartzEnum.JOB_STATUS_RUNNING.getType()).build());
		return R.ok();
	}

	/**
	 * 启动定时任务
	 * @param jobId
	 * @return
	 */
	@AutoLog("立刻执行定时任务")
	@PostMapping("/run-job/{id}")
	@Operation(description = "立刻执行定时任务")
	public R runJob(@PathVariable("id") Long jobId) {
		SysJob querySysJob = this.sysJobService.getById(jobId);
		return TaskUtil.runOnce(scheduler, querySysJob) ? R.ok() : R.failed();
	}

	/**
	 * 暂停定时任务
	 * @return
	 */
	@AutoLog("暂停定时任务")
	@PostMapping("/shutdown-job/{id}")
	@Operation(description = "暂停定时任务")
	public R shutdownJob(@PathVariable("id") Long id) {
		SysJob querySysJob = this.sysJobService.getById(id);
		// 更新定时任务状态条件，运行状态2更新为暂停状态3
		this.sysJobService.updateById(SysJob.builder()
			.jobId(querySysJob.getJobId())
			.jobStatus(QuartzEnum.JOB_STATUS_NOT_RUNNING.getType())
			.build());
		taskUtil.pauseJob(querySysJob, scheduler);
		return R.ok();
	}

	/**
	 * 唯一标识查询定时执行日志
	 * @return
	 */
	@GetMapping("/job-log")
	@Operation(description = "唯一标识查询定时执行日志")
	public R getJobLog(Page page, SysJobLog sysJobLog) {
		return R.ok(sysJobLogService.page(page, Wrappers.query(sysJobLog)));
	}

	/**
	 * 检验任务名称和任务组联合是否唯一
	 * @return
	 */
	@GetMapping("/is-valid-task-name")
	@Operation(description = "检验任务名称和任务组联合是否唯一")
	public R isValidTaskName(@RequestParam String jobName, @RequestParam String jobGroup) {
		return this.sysJobService
			.count(Wrappers.query(SysJob.builder().jobName(jobName).jobGroup(jobGroup).build())) > 0
					? R.failed("任务重复，请检查此组内是否已包含同名任务") : R.ok();
	}

}

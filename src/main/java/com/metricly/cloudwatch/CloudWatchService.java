package com.metricly.cloudwatch;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricDataResult;
import com.amazonaws.services.cloudwatch.model.ListMetricsResult;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.amazonaws.services.cloudwatch.model.MetricDataQuery;
import com.amazonaws.services.cloudwatch.model.MetricDataResult;
import com.amazonaws.services.cloudwatch.model.MetricStat;
import com.amazonaws.services.cloudwatch.model.Statistic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CloudWatchService {

    public static final int METRIC_STAT_PERIOD_SECONDS = 300;

    private AmazonCloudWatch cloudWatch;

    @Autowired
    public CloudWatchService(AmazonCloudWatch cloudWatch) {
        this.cloudWatch = cloudWatch;
    }

    public Map<String, List<Metric>> listMetrics() {

        List<Metric> metricList = new ArrayList<>();

        ListMetricsResult result = null;
        while (result == null || result.getNextToken() != null) {
            result = cloudWatch.listMetrics();
            metricList.addAll(result.getMetrics());
        }

        return metricList.stream()
                .collect(Collectors.groupingBy(
                        metric -> resourceType(metric.getNamespace())));
    }

    public List<MetricDataResult> metricData(String nameSpace, String metricName, Collection<Dimension> dimensions) {

        Metric metric = new Metric()
                .withNamespace(nameSpace)
                .withMetricName(metricName)
                .withDimensions(dimensions);

        Statistic statistic = Statistic.Maximum;

        MetricStat metricStat = new MetricStat()
                .withMetric(metric)
                .withPeriod(METRIC_STAT_PERIOD_SECONDS)
                .withStat(statistic.name());

        MetricDataQuery metricDataQuery = new MetricDataQuery()
                .withMetricStat(metricStat)
                .withId(resourceType(nameSpace).toLowerCase());

        GetMetricDataRequest request = new GetMetricDataRequest()
                .withMetricDataQueries(metricDataQuery)
                .withStartTime(Date.from(Instant.now().truncatedTo(ChronoUnit.HOURS).minus(Duration.ofDays(1))))
                .withEndTime(Date.from(Instant.now().truncatedTo(ChronoUnit.HOURS)));

        List<MetricDataResult> metricDataList = new ArrayList<>();
        GetMetricDataResult result = null;
        while (result == null || result.getNextToken() != null) {
            result = cloudWatch.getMetricData(request);
            metricDataList.addAll(result.getMetricDataResults());
        }

        return metricDataList;
    }

    private String resourceType(String namespace) {
        return namespace.contains("/") ? namespace.split("/")[1] : namespace;
    }


}

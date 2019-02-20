package com.metricly.cloudwatch;

import com.amazonaws.services.cloudwatch.model.Metric;
import com.amazonaws.services.cloudwatch.model.MetricDataResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("metrics")
public class CloudWatchController {

    private CloudWatchService cloudWatch;

    public CloudWatchController(CloudWatchService cloudWatch) {
        this.cloudWatch = cloudWatch;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Map<String, List<Metric>> listMetrics() {
        return cloudWatch.listMetrics();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/stats")
    public List<List<MetricDataResult>> metricStat() {
        return cloudWatch.listMetrics().values().stream()
                .flatMap(List::stream)
                .map(metric -> cloudWatch.metricData(metric.getNamespace(), metric.getMetricName(), metric.getDimensions()))
                .collect(Collectors.toList());
    }
}

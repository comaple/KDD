package com.easyminning.tag;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-8-31
 * Time: 上午11:29
 * To change this template use File | Settings | File Templates.
 */
@Service
public class StepSeedCache {

    /**
     * 步骤 -> 标签 -> 权重
     */
    public static Map<String,Map<String,Double>> STEP_SEED_MAP = new HashMap<String,Map<String,Double>>();


    /**
     * 关键词(标签) -> 权重
     */
    public static Map<String,Double> SEED_MAP = new HashMap<String, Double>();

    protected static Log logger = LogFactory.getLog(StepSeedCache.class);

    private static String STEP_SEED_FILE_NAME = "stepseed.csv";


    @PostConstruct
    public void init() {
        try {
            File file = new File(StepSeedCache.class.getClassLoader()
                    .getResource(STEP_SEED_FILE_NAME).toURI());
            List<String> lines = FileUtils.readLines(file);

            for (String line : lines)  {
                try {
                    if (line == null || "".equals(line.trim())) continue;

                    String step = line.substring(0, line.indexOf(","));
                    String seed = line.substring(line.indexOf(",")+1,line.indexOf(":"));
                    String weight = line.substring(line.indexOf(":")+1);

                    if (step == null || "".equals(step.trim())) continue;
                    if (seed == null || "".equals(seed.trim())) continue;
                    if (weight == null || !NumberUtils.isNumber(weight)) continue;

                    Map<String,Double> seedMap = STEP_SEED_MAP.get(step);
                    if (seedMap == null) {
                        seedMap = new HashMap<String,Double>();
                        STEP_SEED_MAP.put(step,seedMap);
                    }
                    seedMap.put(seed,Double.valueOf(weight));
                    SEED_MAP.put(seed,Double.valueOf(weight));
                } catch (Exception e) {
                    logger.warn("步骤种子解析行出错：" + line);
                    //e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-dataminning.xml");

        System.out.println(NumberUtils.isNumber("20.011q"));
    }


}

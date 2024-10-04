package com.loan.aggregator.logmasking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class MaskingActionPatternLayout extends PatternLayout {

  private List<MaskingActionPattern> maskingActionPattern = new ArrayList<>();
  private Map<MaskingAction, Pattern> actionMap;

  public void addMaskingActionPattern(MaskingActionPattern maskPattern) {
    if (maskPattern == null || !maskPattern.isValid()) {
      return;
    }
    this.maskingActionPattern.add(maskPattern);
  }

  public String format(String msg) {
    for (Map.Entry<MaskingAction, Pattern> entry : actionMap.entrySet()) {
      Matcher matcher = entry.getValue().matcher(msg);
      msg = matcher.replaceAll(mr -> {
        return entry.getKey().mask(mr.group());
      });
    }
    return msg;
  }

  @Override
  public String doLayout(ILoggingEvent event) {

    return format(super.doLayout(event));

  }

  /*
   * (non-Javadoc)
   * 
   * @see ch.qos.logback.core.pattern.PatternLayoutBase#start()
   */
  @Override
  public void start() {
    this.actionMap = maskingActionPattern
        .stream()
        .filter(e -> e.isEnabled())
        .collect(
            Collectors.groupingBy(
                e -> e.getAction(),
                Collectors.collectingAndThen(
                    Collectors.mapping(f -> f.getPattern(), Collectors.joining("|")),
                    g -> Pattern.compile(g, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE))));
    super.start();
  }
}

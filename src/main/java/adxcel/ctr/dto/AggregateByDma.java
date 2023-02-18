package adxcel.ctr.dto;

import lombok.Data;

@Data
public class AggregateByDma implements Aggregate {
   private Object group;
   private long impr;
   private float ctr;
   private float evpm;
}

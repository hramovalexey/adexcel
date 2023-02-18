package adxcel.ctr.dto;

public interface Aggregate {
    <T extends Object> T getGroup();
    long getImpr();
    float getCtr();
    float getEvpm();
    <T extends Object> void setGroup(T group);
    void setImpr(long imprQty);
    void setCtr(float ctr);
    void setEvpm(float evpm);
}

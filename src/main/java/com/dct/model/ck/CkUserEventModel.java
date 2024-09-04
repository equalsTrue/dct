package com.dct.model.ck;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @version 1.0
 * @Author Vic.Zhao
 * @Date 2024/5/30 18:32
 */
@Entity
@Table(name = "ladder_user_event")
@Data
public class CkUserEventModel {
    @Id
    @Column(name = "id")
    public String id;

    @Column(name = "event")
    public String event;

    @Column(name = "event_time")
    public Long eventTime;

    @Column(name = "job_time")
    public Long  jobTime;

    @Column(name = "idfa")
    public String idfa;

    @Column(name = "app_package")
    public String appPackage;

    @Column(name = "app_version")
    public String appVersion;

    @Column(name = "user_country")
    public String userCountry;

    @Column(name = "server_country")
    public String serverCountry;

    @Column(name = "server_ip")
    public String serverIp;

    @Column(name = "duration")
    public Integer duration;

    @Column(name = "ping")
    public String ping;

    @Column(name = "nu")
    public String nu;

    @Column(name = "day")
    public String day;

    @Column(name = "hour")
    public String hour;

    @Column(name = "ad_revenue")
    public BigDecimal adRevenue;

    @Column(name = "ad_id")
    public String adId;

    @Column(name = "ad_type")
    public String adType;
}

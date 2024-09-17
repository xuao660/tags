package com.yupi.usercenter.contant;

/**
 * @Author xuao
 * @Date 2024/9/16 14:05
 * @Description:队伍状态枚举
 */
public enum TeamStatusEnum {

    PUBLIC("公开",0),
    PRIVATE("私密",1),
    ENCRYPT("加密",2);

    private String description;
    private int status;

    TeamStatusEnum (String description,int status){
        this.description = description;
        this.status = status;
    }

    public static TeamStatusEnum getEnumBystatus(Integer status){
        if (status == null) {
            return null;
        }
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (TeamStatusEnum value : values) {
            if (value.status == status) {
                return value;
            }
        }
        return null;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

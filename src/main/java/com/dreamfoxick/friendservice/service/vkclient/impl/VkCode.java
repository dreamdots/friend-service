package com.dreamfoxick.friendservice.service.vkclient.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VkCode {
    TWENTY_FIVE_FRIENDS_GET_CALLS("var arr = [%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d," +
            "%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d];\n" +
            "var var1 = API.friends.get({\"user_id\":arr[0],\"fields\":\"sex\"});\n" +
            "var var2 = API.friends.get({\"user_id\":arr[1],\"fields\":\"sex\"});\n" +
            "var var3 = API.friends.get({\"user_id\":arr[2],\"fields\":\"sex\"});\n" +
            "var var4 = API.friends.get({\"user_id\":arr[3],\"fields\":\"sex\"});\n" +
            "var var5 = API.friends.get({\"user_id\":arr[4],\"fields\":\"sex\"});\n" +
            "var var6 = API.friends.get({\"user_id\":arr[5],\"fields\":\"sex\"});\n" +
            "var var7 = API.friends.get({\"user_id\":arr[6],\"fields\":\"sex\"});\n" +
            "var var8 = API.friends.get({\"user_id\":arr[7],\"fields\":\"sex\"});\n" +
            "var var9 = API.friends.get({\"user_id\":arr[8],\"fields\":\"sex\"});\n" +
            "var var10 = API.friends.get({\"user_id\":arr[9],\"fields\":\"sex\"});\n" +
            "var var11 = API.friends.get({\"user_id\":arr[10],\"fields\":\"sex\"});\n" +
            "var var12 = API.friends.get({\"user_id\":arr[11],\"fields\":\"sex\"});\n" +
            "var var13 = API.friends.get({\"user_id\":arr[12],\"fields\":\"sex\"});\n" +
            "var var14 = API.friends.get({\"user_id\":arr[13],\"fields\":\"sex\"});\n" +
            "var var15 = API.friends.get({\"user_id\":arr[14],\"fields\":\"sex\"});\n" +
            "var var16 = API.friends.get({\"user_id\":arr[15],\"fields\":\"sex\"});\n" +
            "var var17 = API.friends.get({\"user_id\":arr[16],\"fields\":\"sex\"});\n" +
            "var var18 = API.friends.get({\"user_id\":arr[17],\"fields\":\"sex\"});\n" +
            "var var19 = API.friends.get({\"user_id\":arr[18],\"fields\":\"sex\"});\n" +
            "var var20 = API.friends.get({\"user_id\":arr[19],\"fields\":\"sex\"});\n" +
            "var var21 = API.friends.get({\"user_id\":arr[20],\"fields\":\"sex\"});\n" +
            "var var22 = API.friends.get({\"user_id\":arr[21],\"fields\":\"sex\"});\n" +
            "var var23 = API.friends.get({\"user_id\":arr[22],\"fields\":\"sex\"});\n" +
            "var var24 = API.friends.get({\"user_id\":arr[23],\"fields\":\"sex\"});\n" +
            "var var25 = API.friends.get({\"user_id\":arr[24],\"fields\":\"sex\"});\n" +
            "return [var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, " +
            "var12, var13, var14, var15, var16, var17, var18, var19, var20, var21, var22, " +
            "var23, var24, var25];"),

    TWENTY_FRIENDS_GET_CALLS("var arr = [%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d" +
            ",%d,%d,%d,%d,%d,%d];\n" +
            "var var1 = API.friends.get({\"user_id\":arr[0],\"fields\":\"sex\"});\n" +
            "var var2 = API.friends.get({\"user_id\":arr[1],\"fields\":\"sex\"});\n" +
            "var var3 = API.friends.get({\"user_id\":arr[2],\"fields\":\"sex\"});\n" +
            "var var4 = API.friends.get({\"user_id\":arr[3],\"fields\":\"sex\"});\n" +
            "var var5 = API.friends.get({\"user_id\":arr[4],\"fields\":\"sex\"});\n" +
            "var var6 = API.friends.get({\"user_id\":arr[5],\"fields\":\"sex\"});\n" +
            "var var7 = API.friends.get({\"user_id\":arr[6],\"fields\":\"sex\"});\n" +
            "var var8 = API.friends.get({\"user_id\":arr[7],\"fields\":\"sex\"});\n" +
            "var var9 = API.friends.get({\"user_id\":arr[8],\"fields\":\"sex\"});\n" +
            "var var10 = API.friends.get({\"user_id\":arr[9],\"fields\":\"sex\"});\n" +
            "var var11 = API.friends.get({\"user_id\":arr[10],\"fields\":\"sex\"});\n" +
            "var var12 = API.friends.get({\"user_id\":arr[11],\"fields\":\"sex\"});\n" +
            "var var13 = API.friends.get({\"user_id\":arr[12],\"fields\":\"sex\"});\n" +
            "var var14 = API.friends.get({\"user_id\":arr[13],\"fields\":\"sex\"});\n" +
            "var var15 = API.friends.get({\"user_id\":arr[14],\"fields\":\"sex\"});\n" +
            "var var16 = API.friends.get({\"user_id\":arr[15],\"fields\":\"sex\"});\n" +
            "var var17 = API.friends.get({\"user_id\":arr[16],\"fields\":\"sex\"});\n" +
            "var var18 = API.friends.get({\"user_id\":arr[17],\"fields\":\"sex\"});\n" +
            "var var19 = API.friends.get({\"user_id\":arr[18],\"fields\":\"sex\"});\n" +
            "var var20 = API.friends.get({\"user_id\":arr[19],\"fields\":\"sex\"});\n" +
            "return [var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, " +
            "var12, var13, var14, var15, var16, var17, var18, var19, var20];"),

    TEN_FRIENDS_GET_CALLS("var arr = [%d, %d, %d, %d, %d, %d, %d, %d, %d, %d];\n" +
            "var var1 = API.friends.get({\"user_id\":arr[0],\"fields\":\"sex\"});\n" +
            "var var2 = API.friends.get({\"user_id\":arr[1],\"fields\":\"sex\"});\n" +
            "var var3 = API.friends.get({\"user_id\":arr[2],\"fields\":\"sex\"});\n" +
            "var var4 = API.friends.get({\"user_id\":arr[3],\"fields\":\"sex\"});\n" +
            "var var5 = API.friends.get({\"user_id\":arr[4],\"fields\":\"sex\"});\n" +
            "var var6 = API.friends.get({\"user_id\":arr[5],\"fields\":\"sex\"});\n" +
            "var var7 = API.friends.get({\"user_id\":arr[6],\"fields\":\"sex\"});\n" +
            "var var8 = API.friends.get({\"user_id\":arr[7],\"fields\":\"sex\"});\n" +
            "var var9 = API.friends.get({\"user_id\":arr[8],\"fields\":\"sex\"});\n" +
            "var var10 = API.friends.get({\"user_id\":arr[9],\"fields\":\"sex\"});\n" +
            "return [var1, var2, var3, var4, var5, var6, var7, var8, var9, var10];"),

    FIVE_FRIENDS_GET_CALLS("var arr = [%d, %d, %d, %d, %d];\n" +
            "var var1 = API.friends.get({\"user_id\":arr[0],\"fields\":\"sex\"});\n" +
            "var var2 = API.friends.get({\"user_id\":arr[1],\"fields\":\"sex\"});\n" +
            "var var3 = API.friends.get({\"user_id\":arr[2],\"fields\":\"sex\"});\n" +
            "var var4 = API.friends.get({\"user_id\":arr[3],\"fields\":\"sex\"});\n" +
            "var var5 = API.friends.get({\"user_id\":arr[4],\"fields\":\"sex\"});\n" +
            "return [var1, var2, var3, var4, var5];");

    private final String code;
}

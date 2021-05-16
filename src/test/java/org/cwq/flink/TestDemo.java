package org.cwq.flink;

import java.util.Arrays;

/**
 * @Description TODO
 * @Author 承文全
 * @Date 2021/4/5 11:17
 * @Version 1.0
 */
public class TestDemo {

    public static void main(String[] args) {

        int[] arr = {1,7,3,2,9,5,4,8,6};

        //冒泡
        for(int i=0;i<arr.length;i++){
            for (int j=0;j<arr.length-1;j++){
                if(arr[j]>arr[j+1]){
                    int tmp = arr[j];
                    arr[j]=arr[j+1];
                    arr[j+1]=tmp;
                }
            }
        }
        System.out.println(Arrays.toString(arr));







        //二分查找
        int[] arry = {0,1,2,3,4,5,6,7,8,9};
        System.out.println(findVal(arry, 0, arry.length, 3));
    }


    /**
     * 二分查找
     * @param array 查找对象
     * @param start 开始位置
     * @param end 结束位置
     * @param findVal 查找值
     * @return
     */
    public static int findVal(int[] array,int start,int end,int findVal){
        int i = (start + end)/2;
        if(array[i]==findVal){
            return i;
        }
        if(array[i]>findVal){//向左找
            return findVal(array,start,i-1,findVal);
        }else if(array[i]<findVal){//向右找
            return findVal(array,i+1,end,findVal);
        }
            return -1;
    }


}

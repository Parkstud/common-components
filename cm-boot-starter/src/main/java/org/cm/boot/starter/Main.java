package org.cm.boot.starter;

import java.util.Arrays;

/**
 * @author miao.chen01@hand-china.com 2021-04-08
 */
public class Main {

    public static void main(String[] args) {
        int arr[] = {5, 5, 3, 6, 2, 8, 7};
        quickSort(arr, 0, arr.length - 1);
    }


    static void quickSort(int[] arr, int left, int right) {
        if (left >= right) {
            return;
        }
        int leftIndex = left;
        int index = arr[left];
        while (left < right) {
            while (right > left) {
                if (index > arr[right]) {
                    break;
                }
                right--;
            }
            while (left < right) {
                if (index < arr[left]) {
                    break;
                }
                left++;
            }
            if(arr[left]>arr[right]){
                int temp = arr[left];
                arr[left] = arr[right];
                arr[right] = temp;
            }

        }
        arr[leftIndex] = arr[left];
        arr[left] = index;


        System.out.println(Arrays.toString(arr));
        System.out.println(left + "\t" + right);
        quickSort(arr, left, right / 2 );
        quickSort(arr, right / 2 , right);
    }

}

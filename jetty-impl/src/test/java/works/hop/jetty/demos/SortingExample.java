package works.hop.jetty.demos;

import org.junit.Test;

public class SortingExample {

    void bubbleSort(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                //Bubble up the larger element to the end of the list
                if (array[j] > array[j + 1]) {
                    int temp = array[j + 1];
                    array[j + 1] = array[j];
                    array[j] = temp;
                }
            }
        }
    }

    void selectionSort(int[] array) {
        // One by one move boundary of unsorted sub-array
        for (int i = 0; i < array.length; i++) {
            int min = i;
            for (int j = i + 1; j < array.length; j++) {
                // Find the minimum element in unsorted array
                if (array[j] < array[min]) {
                    min = j;
                }
            }
            // Swap the found minimum element with the first element
            int temp = array[i];
            array[i] = array[min];
            array[min] = temp;
        }
    }

    void insertionSort(int[] array) {
        for (int i = 1; i < array.length; i++) {
            int key = array[i];
            int j = i - 1;
            //Move elements of arr[0..i-1], that are greater than key, to one position ahead of their current position
            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j];
                j -= 1;
            }
            array[j + 1] = key;
        }
    }

    @Test
    public void testBubbleSort() {
        int arr[] = {64, 25, 12, 22, 11};
        bubbleSort(arr);

        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    @Test
    public void testSelectionSort() {
        int arr[] = {64, 25, 12, 22, 11};
        selectionSort(arr);

        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    @Test
    public void testInsertionSort() {
        int arr[] = {64, 25, 12, 22, 11};
        insertionSort(arr);

        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }
}

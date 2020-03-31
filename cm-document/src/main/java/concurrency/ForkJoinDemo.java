package concurrency;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import lombok.extern.slf4j.Slf4j;

/**
 * Fork/Join 框架案例
 *
 * @author parkstud@qq.com 2020-03-31
 */
@Slf4j
public class ForkJoinDemo {

    public static void main(String[] args) {
        //        ForkJoinPool fjp = new ForkJoinPool();
        ForkJoinPool fjp = ForkJoinPool.commonPool();
        double[] nums = new double[100000];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = i;
        }

        log.info("A portion of the original sequence:");
        for (int i = 0; i < 100; i++) {
            log.info(nums[i] + "  ");
        }

        SqrtTransForm task = new SqrtTransForm(nums, 0, nums.length);
        fjp.invoke(task);
        for (int i = 0; i < 100; i++) {
            log.info(nums[i] + "  ");
        }

    }
}

class SqrtTransForm extends RecursiveAction {

    final int seqThreadHold = 1000;
    double[] data;
    int start, end;

    public SqrtTransForm(double[] data, int start, int end) {
        this.data = data;
        this.start = start;
        this.end = end;
    }

    @Override
    protected void compute() {
        if (end - start < seqThreadHold) {
            for (int i = start; i < end; i++) {
                data[i] = Math.sqrt(data[i]);
            }
        } else {
            int middle = (start + end) / 2;
            invokeAll(new SqrtTransForm(data, start, middle), new SqrtTransForm(data, middle, end));
        }
    }
}

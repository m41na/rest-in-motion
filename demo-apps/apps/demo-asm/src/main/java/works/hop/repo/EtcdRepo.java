package works.hop.repo;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import com.ibm.etcd.api.PutRequest;
import com.ibm.etcd.api.PutResponse;
import com.ibm.etcd.api.RangeResponse;
import com.ibm.etcd.client.EtcdClient;
import com.ibm.etcd.client.KvStoreClient;
import com.ibm.etcd.client.kv.KvClient;
import com.ibm.etcd.client.lease.LeaseClient;
import com.ibm.etcd.client.lock.LockClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EtcdRepo {

    private static EtcdRepo instance;

    private final KvStoreClient client;

    private EtcdRepo(){
        this.client = EtcdClient.forEndpoint("localhost", 2379).withPlainText().build();
        init();
    }

    public static EtcdRepo instance(){
        if(instance == null){
            instance = new EtcdRepo();
        }
        return instance;
    }

    private void init(){
    }

    public KvClient kvClient(){
        return client.getKvClient();
    }

    public LeaseClient leaseClient(){
        return client.getLeaseClient();
    }

    public LockClient lockClient(){
        return client.getLockClient();
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        KvClient client = instance().kvClient();
        ByteString key = ByteString.copyFrom("firstName".getBytes());
        ByteString value = ByteString.copyFrom("Steve".getBytes());
        PutResponse putResult = client.put(key, value).sync();
        System.out.println(putResult);
        RangeResponse getResult = client.get(key).asPrefix().sync();
        System.out.println(getResult);
        PutRequest req = PutRequest.newBuilder().setKey(key).setValue(value).build();
        ListenableFuture<PutResponse> asyncPut = client.put(req);
        PutResponse asyncPutResponse = asyncPut.get(1000, TimeUnit.MILLISECONDS);
        System.out.println(asyncPutResponse);
    }
}

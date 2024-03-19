package com.atguigu.cloud.controller;


import com.atguigu.cloud.entities.PayDTO;
import com.atguigu.cloud.resp.ResultData;
import jakarta.annotation.Resource;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class OrderContorller {

    //
    // public static final String PaymentSrv_URL= "http://localhost:8001";
    public static final String PaymentSrv_URL= "http://cloud-payment-service";

    @Resource
    private RestTemplate restTemplate;

    @GetMapping(value = "/consumer/pay/add")
    public ResultData addOrder(PayDTO payDTO){
        return restTemplate.postForObject(PaymentSrv_URL + "/pay/add", payDTO, ResultData.class);
    }

    @GetMapping(value = "/consumer/pay/get/{id}")
    public ResultData getOrder(@PathVariable("id") Integer id){
        return restTemplate.getForObject(PaymentSrv_URL + "/pay/get/"+id, ResultData.class, id);
    }

//    @GetMapping(value = "/consumer/pay/update")
//    public void updateOrder(PayDTO payDTO){
//        restTemplate.put(PaymentSrv_URL+"/pay/update",payDTO);
//        //return ResultData.success("更新成功");
//    }
//
//    @GetMapping(value = "/consumer/pay/delete/{id}")
//    public void deleteOrder(@PathVariable("id") Integer id){
//        restTemplate.delete(PaymentSrv_URL+"/pay/delete/"+id);
//        //return ResultData.success("删除成功");
//    }

    @GetMapping("/consumer/pay/del/{id}")
    public ResultData deleteOrder(@PathVariable("id") Integer id){
        ResponseEntity<ResultData> response = restTemplate.exchange(PaymentSrv_URL + "/pay/del/" + id, HttpMethod.DELETE, null, ResultData.class, id);
        ResultData result = response.getBody();
        return result;
        }

    @GetMapping("/consumer/pay/update")
    public ResultData updateOrder(PayDTO payDTO){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PayDTO> requestEntity = new HttpEntity<>(payDTO, headers);
        ResponseEntity<ResultData > response = restTemplate.exchange(PaymentSrv_URL+"/pay/update", HttpMethod.PUT, requestEntity, ResultData.class, payDTO);
        ResultData result = response.getBody();
        return result;
    }

    @GetMapping(value = "/consumer/pay/get/info")
    private String getInfoByConsul(){
        return restTemplate.getForObject(PaymentSrv_URL+ "/pay/get/info", String.class);
    }

    @Resource
    private DiscoveryClient discoveryClient;
    @GetMapping("/consumer/discovery")
    public String discovery()
    {
        List<String> services = discoveryClient.getServices();
        for (String element : services) {
            System.out.println(element);
        }

        System.out.println("===================================");

        List<ServiceInstance> instances = discoveryClient.getInstances("cloud-payment-service");
        for (ServiceInstance element : instances) {
            System.out.println(element.getServiceId()+"\t"+element.getHost()+"\t"+element.getPort()+"\t"+element.getUri());
        }

        return instances.get(0).getServiceId()+":"+instances.get(0).getPort();
    }


}

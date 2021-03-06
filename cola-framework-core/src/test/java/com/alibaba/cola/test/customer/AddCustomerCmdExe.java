package com.alibaba.cola.test.customer;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.extension.ExtensionExecutor;
import com.alibaba.cola.logger.Logger;
import com.alibaba.cola.logger.LoggerFactory;
import com.alibaba.cola.test.customer.convertor.CustomerConvertorExtPt;
import com.alibaba.cola.test.customer.entity.CustomerEntity;
import com.alibaba.cola.test.customer.validator.extensionpoint.AddCustomerValidatorExtPt;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * AddCustomerCmdExe
 *
 * @author Frank Zhang 2018-01-06 7:48 PM
 */
@Component
public class AddCustomerCmdExe {

    private Logger logger = LoggerFactory.getLogger(AddCustomerCmd.class);

    @Resource
    private ExtensionExecutor extensionExecutor;

    @Resource
    private DomainEventPublisher domainEventPublisher;


    public Response execute(AddCustomerCmd cmd) {
        logger.info("Start processing command:" + cmd);

        //validation
        extensionExecutor.executeVoid(AddCustomerValidatorExtPt.class, cmd.getBizScenario(), extension -> extension.validate(cmd));

        //Convert CO to Entity
        CustomerEntity customerEntity = extensionExecutor.execute(CustomerConvertorExtPt.class, cmd.getBizScenario(), extension -> extension.clientToEntity(cmd));

        //Call Domain Entity for business logic processing
        logger.info("Call Domain Entity for business logic processing..."+customerEntity);
        customerEntity.addNewCustomer();

        //domainEventPublisher.publish(new CustomerCreatedEvent());
        logger.info("End processing command:" + cmd);
        return Response.buildSuccess();
    }
}

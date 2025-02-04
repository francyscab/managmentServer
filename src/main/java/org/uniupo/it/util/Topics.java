package org.uniupo.it.util;

public class Topics {
    public final static String MANAGEMENT_NEW_FAULT_TOPIC = "management/faults/newFault";

    public static final String REGISTER_TRANSACTION_TOPIC = "management/transaction/register";

    public static final String CONSUMABLES_REQUEST_TOPIC = "machines/%s/%s/consumables/request";
    public static final String CONSUMABLES_RESPONSE_TOPIC = "machines/+/+/consumables/response";

    public static final String MANAGEMENT_RESOLVE_FAULT_TOPIC = "management/faults/resolve";
    public static final String MANAGEMENT_REVENUE_TOPIC_RESPONSE = "management/revenue/revenueResponse";
    public static final String MANAGEMENT_REVENUE_TOPIC = "management/%s/%s/revenue";

    public static final String TECHNICIAN_ASSISTANCE_TOPIC = "macchinette/%s/%s/assistance/technician";

    public static final String KILL_SERVICE_TOPIC = "macchinette/%s/%s/killService";

    public static final String NEW_MACHINE_TOPIC = "machineServer/%s/%s/newMachine";

}

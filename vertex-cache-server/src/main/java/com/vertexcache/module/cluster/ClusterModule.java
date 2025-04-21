package com.vertexcache.module.cluster;

import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;


public class ClusterModule extends Module {

    private ClusterConfigLoader clusterConfig;

    @Override
    protected void onValidate() {

    }

    @Override
    protected void onStart() {
        try {

            //validate();

            //this.clusterConfig = Config.getInstance().getClusterConfigLoader();

            /*
            ClusterTopologyValidator validator = new ClusterTopologyValidator();
            String error = validator.validate(clusterConfig.getAllClusterNodes());
            if (error != null) {
                reportHealth(ModuleStatus.STARTUP_FAILED, error);
                return;
            }

             */

            reportHealth(ModuleStatus.STARTUP_SUCCESSFUL, "Cluster nodes loaded successfully");

        } catch (Exception e) {
            reportHealth(ModuleStatus.STARTUP_FAILED, "Exception during cluster initialization: " + e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        this.clusterConfig = null;
        setModuleStatus(ModuleStatus.SHUTDOWN_SUCCESSFUL);
    }

    //@Override
    protected void validate() {
       // System.out.println("validate failed here");
        //reportHealth(ModuleStatus.STARTUP_FAILED, "Cluster Validation failed");
        //throw new VertexCacheClusterModuleException("the error message");
    }

    public ClusterConfigLoader getClusterConfig() {
        return clusterConfig;
    }
}

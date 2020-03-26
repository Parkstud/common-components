# Kubernetes学习

k8s是Google开源的容器集群管理系统,基于Docker构建一个容器的调度服务,提供资源调度,均衡容灾,服务注册,动态扩容等功能套件

## 概念

### 基础知识

- **Nodes**:代表K8s平台的工作节点,如一台主机
- **Pods**:在K8s中国,调度的最小粒度不是容器而是一个抽象成的Pod,Pod是一个可以被创建,销毁,调度和管理的最小部署单元.
- **The Life of a Pod**: 包括pod的状态,时间和生命周期,复制控制器等
- **Replication Controllers** :K8s中 最有用的功能,实现复制多个Pod副本
- **Services**:Kubernetes 最外围的单元,通过虚拟访问一个Ip及服务端口,可以访问定义好的Pod
- **Volumes**: 一个能够被容器访问的目录,包含一些数据
- **Labels**: 用于区分Pod,Service Replication Controller的key/value 键值对
- **Accessing the API**: k8s中端口 Ip 代理服务和防火墙规则
- **Kubernetes Web Interface**: 方位Kubernetes Web接口
- **Kubeetl COmmand Line Interface**: K8s命令行接口




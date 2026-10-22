# MTR-VisionUpgrade

**MTR-VisionUpgrade** 是专为 **Minecraft Transit Railway (MTR 4.x)** 设计的**纯客户端**渲染与性能极致优化模组。

---

## 🌟 核心特性与优化内容

1. **车站视锥体裁剪 (Conservative Frustum Culling)**
   - 接入 Fabric `WorldRenderEvents.AFTER_SETUP`，结合相机视锥与方块实体 AABB 裁剪屏幕视野外的屏蔽门（PSD/APG）、门头、玻璃与路线图。
   - 保留离屏门体动画计时，大幅降低超大车站与复杂换乘枢纽中的无效 DrawCall 与矩阵运算。
   - 完美适配光影与阴影渲染通道，防止阴影投影漏渲染。

2. **站台路线图后缀跨度缓存 (RouteMapSpanCache)**
   - 拦截并缓存 `RenderRouteBase` 沿站台的长距离逐格方块探测，将原本每帧重复执行的 $O(N^2)$ 邻近方块检查优化为 $O(N)$。

3. **OpenGL 矩阵上传 DirectBuffer 复用 (MatrixUploadBuffer)**
   - 解决 MTR 底层映射库在矩阵变换上传时频繁执行 `ByteBuffer.allocate(64)` 导致的堆外内存分配与垃圾回收压力。
   - 提供 ThreadLocal 64 字节 DirectBuffer 零分配复用。

4. **无分配哈希算法覆写 (No-Alloc HashCode)**
   - 覆写 `MaterialProperties` 与 `VertexAttributeState` 的 `hashCode()`，消除高频材质与渲染管线遍历中 `Objects.hash(...)` 产生的临时变长数组（`Object[]`）开销。

5. **渲染队列双缓冲与对象池 (RenderCallbackPool)**
   - 覆写 `scheduleRender`，消除空标识符重复分配，提供队列双缓冲与列表对象池复用。

6. **轨道贝塞尔几何缓存 (RailGeometryCache)**
   - 采样并缓存静态轨道的 3D 贝塞尔曲线点阵坐标，避免每帧重复进行密集的三角函数运算。

7. **Iris 光影拓扑排序加速 (RenderOrderCache)**
   - 搭配 Iris / Sodium 光影时，透明图层强连通分量（SCC）与反馈弧集（Feedback Arc Set, FAS）结果跨帧缓存，将每帧约 40ms 的光影排序耗时降低至 ~0.5ms。

8. **遮挡剔除安全点让步 (OcclusionSafepointMixin)**
   - 优化深层体素射线遍历，定期调用 `Thread.yield()` 避免 JVM JIT 安全点饥饿，消除微卡顿。

---

## 🔒 100% 纯客户端友好

* **无需服务端安装**：仅在客户端运行（`"environment": "client"`）。
* **协议完全兼容**：不修改任何 MTR 原始游戏机制、网络通信数据包或存档结构。
* **随时加入原版服务器**：兼容任何使用官方原版 MTR 4.x 的局域网、单人存档或多人服务器。

---

## 🛠️ 构建与编译

* **Minecraft 版本**：1.20.1 Fabric
* **Fabric Loader**：>= 0.16.10
* **Fabric API**：>= 0.92.6+1.20.1
* **Java 版本**：Java 17 (JDK 17+)

```bash
# 克隆仓库
git clone https://github.com/harry33333333/MTR-VisionUpgrade.git
cd MTR-VisionUpgrade

# 编译生成 Jar
./gradlew build
```

编译产物位于 `build/libs/MTR-VisionUpgrade-<version>.jar`。

---

## 📄 开源许可证

本项目基于 [MIT License](LICENSE) 开源。
致谢 MTR、MTR-YanYang 社区以及 Fabric 社区的优秀开源工作。

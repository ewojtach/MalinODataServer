package server.malin.device

import java.util

import org.apache.olingo.commons.api.edm.{EdmPrimitiveTypeKind, FullQualifiedName}
import org.apache.olingo.commons.api.edm.provider._

/**
  * Created by ewa on 14.08.2017.
  */
class DeviceEdmProvider (namespace: String, container_name: String, container: FullQualifiedName,
                         et_device_name: String, et_device_fqn: FullQualifiedName, es_devices_name: String)
  extends CsdlAbstractEdmProvider {

  override def getEntityType(entityTypeName: FullQualifiedName): CsdlEntityType = {
    if (entityTypeName.equals(et_device_fqn)) {
      val mac = new CsdlProperty().setName("MAC").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName).setNullable(false)
      val deviceType = new CsdlProperty().setName("TYPE").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName)
      val description = new CsdlProperty().setName("DESCRIPTION").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName)
      val status = new CsdlProperty().setName("STATUS").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName)


      val propertyRef = new CsdlPropertyRef()
      propertyRef.setName("MAC")

      val entityType = new CsdlEntityType()
      entityType.setName(et_device_name)
      entityType.setProperties(java.util.Arrays.asList(mac, deviceType, description, status))
      entityType.setKey(java.util.Collections.singletonList(propertyRef))

      return entityType
    }
    null
  }

  override def getEntitySet(entityContainer: FullQualifiedName, entitySetName: String): CsdlEntitySet = {
    if (entityContainer.equals(container)){
      if (entitySetName.equals(es_devices_name)){
        val entitySet = new CsdlEntitySet()
        entitySet.setName(es_devices_name)
        entitySet.setType(et_device_fqn)

        return entitySet
      }
    }
    null
  }

  override def getEntityContainer: CsdlEntityContainer = {
    val entitySets = new util.ArrayList[CsdlEntitySet]()
    entitySets.add(getEntitySet(container, es_devices_name))

    val entityContainer = new CsdlEntityContainer()
    entityContainer.setName(container_name)
    entityContainer.setEntitySets(entitySets)

    entityContainer
  }

  override def getSchemas: util.List[CsdlSchema] = {
    val schema = new CsdlSchema()
    schema.setNamespace(namespace)

    val entityTypes = new util.ArrayList[CsdlEntityType]()
    entityTypes.add(getEntityType(et_device_fqn))
    schema.setEntityTypes(entityTypes)

    schema.setEntityContainer(getEntityContainer())

    val schemas = new util.ArrayList[CsdlSchema]()
    schemas.add(schema)

    schemas
  }

  override def getEntityContainerInfo(entityContainerName: FullQualifiedName): CsdlEntityContainerInfo = {
    if (entityContainerName == null || entityContainerName.equals(container)) {
      val entityContainerInfo = new CsdlEntityContainerInfo()
      entityContainerInfo.setContainerName(container)
      return entityContainerInfo
    }
    null
  }
}

object DeviceEdmProvider {
  private val namespace = "OData.Device"

  private val container_name = "Container"
  private val container = new FullQualifiedName(namespace, container_name)

  private val et_device_name = "Device"
  private val et_device_fqn = new FullQualifiedName(namespace, et_device_name)

  private val es_devices_name = "Devices"

  def apply(): DeviceEdmProvider = new DeviceEdmProvider(namespace, container_name, container, et_device_name, et_device_fqn, es_devices_name)
}
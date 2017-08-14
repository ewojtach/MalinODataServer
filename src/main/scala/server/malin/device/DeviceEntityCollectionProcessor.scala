package server.malin.device
import java.net.URI

import org.apache.olingo.commons.api.data._
import org.apache.olingo.commons.api.edm.EdmEntitySet
import org.apache.olingo.commons.api.format.ContentType
import org.apache.olingo.commons.api.http.{HttpHeader, HttpStatusCode}
import org.apache.olingo.server.api.{OData, ODataRequest, ODataResponse, ServiceMetadata}
import org.apache.olingo.server.api.uri.{UriInfo, UriResourceEntitySet}
import org.apache.olingo.server.api.OData
import org.apache.olingo.server.api.ServiceMetadata
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions

/**
  * Created by ewa on 14.08.2017.
  */
class DeviceEntityCollectionProcessor extends org.apache.olingo.server.api.processor.EntityCollectionProcessor {


  private var odata : OData = null
  private var serviceMetadata : ServiceMetadata = null

  override def init(odata: OData, serviceMetadata: ServiceMetadata): Unit = {
    this.odata = odata
    this.serviceMetadata = serviceMetadata
  }

  override def readEntityCollection(request: ODataRequest, response: ODataResponse,
                                    uriInfo: UriInfo, responseFormat: ContentType): Unit = {

    val resourcePaths = uriInfo.getUriResourceParts()
    val uriResourceEntitySet = resourcePaths.get(0).asInstanceOf[UriResourceEntitySet]
    val edmEntitySet = uriResourceEntitySet.getEntitySet()

    val entitySet = getData(edmEntitySet)


    val oDataSerializer = odata.createSerializer(responseFormat)

    val edmEntityType = edmEntitySet.getEntityType()

    val id = request.getRawBaseUri + "/" + edmEntitySet.getName
    val contextURL = ContextURL.`with`().entitySet(edmEntitySet).build()
    val opts = EntityCollectionSerializerOptions.`with`().id(id).contextURL(contextURL).build()
    val serializerResult = oDataSerializer.entityCollection(serviceMetadata, edmEntityType, entitySet, opts)
    val serializedContent = serializerResult.getContent

    response.setContent(serializedContent)
    response.setStatusCode(HttpStatusCode.OK.getStatusCode)
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString)
  }

  def getData(edmEntitySet: EdmEntitySet) : EntityCollection = {
    val devicesCollection = new EntityCollection()
    val entity = new Entity()
      .addProperty(new Property(null, "MAC", ValueType.PRIMITIVE, "123456"))
      .addProperty(new Property(null, "DESCRIPTION", ValueType.PRIMITIVE, "drzwi wejsciowe - termometr"))
      .addProperty(new Property(null, "TYPE", ValueType.PRIMITIVE, "termometr"))
      .addProperty(new Property(null, "STATUS", ValueType.PRIMITIVE, "active"))
    val entity2 = new Entity()
      .addProperty(new Property(null, "MAC", ValueType.PRIMITIVE, "223344"))
      .addProperty(new Property(null, "DESCRIPTION", ValueType.PRIMITIVE, "drzwi wejsciowe - alarm"))
      .addProperty(new Property(null, "TYPE", ValueType.PRIMITIVE, "alarm"))
      .addProperty(new Property(null, "STATUS", ValueType.PRIMITIVE, "active"))

    entity.setId(createId("Devices", "123456"))
    entity2.setId(createId("Devices", "223344"))
    devicesCollection.getEntities.add(entity)
    devicesCollection.getEntities.add(entity2)

    devicesCollection
  }

  def createId(entitySetName: String, key: Object): URI = {
    new URI(entitySetName + "('" + String.valueOf(key) + "')")
  }


}

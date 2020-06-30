package com.mita.retrofit_api;



import com.google.gson.JsonObject;
import com.mita.mqtt.athlete.model.ActivityPlansResponse;
import com.mita.mqtt.athlete.model.ActivitySummaryResponse;
import com.mita.mqtt.athlete.model.AthleteActivitySummaryModel;
import com.mita.mqtt.athlete.model.AthletePlanPurchaseSummaryResModel;
import com.mita.mqtt.athlete.model.AthletePlansResponseModel;
import com.mita.mqtt.athlete.model.AthleteTodaysRunResponseModel;
import com.mita.mqtt.athlete.model.AthleteUpcomingPlanResponseModel;
import com.mita.mqtt.athlete.model.AthleteDetailsResponseModel;
import com.mita.mqtt.athlete.model.CoachListResponse;
import com.mita.mqtt.athlete.model.PastRunAudioFeedBackModel;
import com.mita.mqtt.athlete.model.PlansResponseModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {


    @Headers("Content-Type: application/json")
    @POST("Mita/athleteController/atheletSave")
    Call<JsonObject> atheletSaveData(@Body JsonObject jsonObject);

    // get Athlete Details by Phoone Number API
    @GET("Mita/athleteController/getAtheletByPhone/{value}")
    Call<AthleteDetailsResponseModel> getAthleteDetailsByPhoneNumber(@Path(value = "value") String value);

    // Update Profle Details API
    @PUT("Mita/athleteController/updateAtheletProfile/{value}")
    Call<JsonObject> UpdateAthleteProfile(@Path(value = "value") String value, @Body JsonObject jsonObject);

    @Multipart
    @POST("Mita/athleteController/uploadProfileForAthelet")
    Call<ResponseBody> updateProfilePhoto(@Part("atheId") RequestBody coachId, @Part MultipartBody.Part image);

    @GET("Mita/coachController/coachGetAll")
    Call<CoachListResponse> getAllCoaches();

    @GET("Mita/athleteController/getAllAtheletById/{value}")
    Call<AthleteDetailsResponseModel> getAtheletById(@Path(value = "value") String value);

    @GET("Mita/MetaCoachAlthController/getCoachDetailsByMetaPlanId")
    Call<CoachListResponse> getCoachByPlanId(@Query("metaPlanId") String metaPlanId);



    @GET("Mita/activityRealTimeController/getAllActivityRealTimeByRunActivityId")//2168
    Call<AthleteActivitySummaryModel> getAthleteRunActivity(@Query("runActivityId") String athleteeId);

    // get audio streem passing by id
    @GET("Mita/feedBackController/getFeedBackById/{value}")
    Call<PastRunAudioFeedBackModel> getFeedBackAudioById(@Path(value = "value") String value);


    // Get Coach Details passing Coach ID
    @GET("Mita/coachController/coachGetById/{value}")
    Call<AthleteDetailsResponseModel> getCoachDetailsById(@Path(value = "value") String value);


    // Get Plans details Summary details passing Plan ID
    @GET("Mita/MetaCoachAlthController/MetaCoachAlthId/{value}")
    Call<AthletePlanPurchaseSummaryResModel> getPlanDetailsSummary(@Path(value = "value") String value);


    // get All Athlete Plans
    @GET("Mita/MetaCoachAlthController/getAllMetaPlanForAlthSelect")
    Call<AthletePlansResponseModel> getAthletsPlans();

    @GET("Mita/MetaCoachAlthController/addAlthToMetaCoachAlth")
    Call<AthletePlansResponseModel> addAlthToMetaCoachAlth(@Query("metaCoachAlthId") String metaCoachAlthId, @Query("athUserId") String athUserId);

    @GET("Mita/athleteController/getFinishedRunForAthelet")
    Call<AthleteUpcomingPlanResponseModel> getFinishedRunForAthelet(@Query("athUserId") String athUserId);

    @GET("Mita/athleteController/getUpcomingRunForAthelet")
    Call<AthleteUpcomingPlanResponseModel> getUpcomingRunForAthelet(@Query("athUserId") String athUserId);

    //activitySummary/activitySummaryUpdateStatusAndDate?runActId=1_1_0&status=1
    @GET("Mita/activitySummary/activitySummaryUpdateStatusAndDate")
    Call<ResponseBody> activitySummaryUpdateStatusAndDate(@Query("runActId") String runActId,
                                                          @Query("status") String status);

    @GET("Mita/athleteController/getTodaysRunForAthelet")
    Call<AthleteTodaysRunResponseModel> getTodaysRunForAthelet(@Query("athUserId") String athUserId,
                                                               @Query("date") String date);


    @GET("Mita/athleteController/getValidateByAthelActivityId")
    Call<AthleteTodaysRunResponseModel> getValidateByAthelActivityId(@Query("runActivityId") String runActivityId);



}